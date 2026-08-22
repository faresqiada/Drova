package com.example.presentation.captain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.di.ServiceLocator
import com.example.domain.model.*
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.CaptainRepository
import com.example.domain.repository.OrderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CaptainViewModel(
    private val authRepository: AuthRepository = ServiceLocator.authRepository,
    private val captainRepository: CaptainRepository = ServiceLocator.captainRepository,
    private val orderRepository: OrderRepository = ServiceLocator.orderRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(CaptainMainTab.HOME)
    val selectedTab: StateFlow<CaptainMainTab> = _selectedTab.asStateFlow()

    private val _selectedHistoryTask = MutableStateFlow<DeliveryTask?>(null)
    val selectedHistoryTask: StateFlow<DeliveryTask?> = _selectedHistoryTask.asStateFlow()

    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    private val _payoutDialogState = MutableStateFlow(false)
    val payoutDialogState: StateFlow<Boolean> = _payoutDialogState.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    val currentUser: StateFlow<User?> = authRepository.currentUser
    val isOnline: StateFlow<Boolean> = captainRepository.isOnline
    val captainMode: StateFlow<CaptainMode> = captainRepository.captainMode
    val earnings: StateFlow<CaptainEarnings> = captainRepository.earnings
    val shiftData: StateFlow<CaptainShiftData> = captainRepository.shiftData
    val availableTasks: StateFlow<List<DeliveryTask>> = captainRepository.availableTasks
    val activeTask: StateFlow<DeliveryTask?> = captainRepository.activeTask
    val completedTasks: StateFlow<List<DeliveryTask>> = captainRepository.completedTasks
    val transactions: StateFlow<List<CaptainTransaction>> = captainRepository.transactions
    val notifications: StateFlow<List<CaptainNotification>> = captainRepository.notifications

    val unreadNotificationsCount: StateFlow<Int> = captainRepository.notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingRequestsCount: StateFlow<Int> = captainRepository.availableTasks.map { list ->
        list.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectTab(tab: CaptainMainTab) {
        _selectedTab.value = tab
    }

    fun toggleOnline(online: Boolean) {
        viewModelScope.launch {
            captainRepository.toggleOnlineStatus(online)
            _userFeedbackMessage.value = if (online) "أنت الآن متصل وجاهز لاستقبال الطلبات" else "تم إيقاف الاتصال مؤقتاً"
        }
    }

    fun setCaptainMode(mode: CaptainMode, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val success = captainRepository.setCaptainMode(mode)
            if (success) {
                _userFeedbackMessage.value = "تم التبديل إلى ${mode.titleAr}"
            } else {
                _userFeedbackMessage.value = "لا يمكن تبديل نظام العمل أثناء وجود رحلة جارية"
            }
            onComplete?.invoke(success)
        }
    }

    fun acceptTask(orderId: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val success = captainRepository.acceptTask(orderId)
            if (success) {
                _userFeedbackMessage.value = "تم قبول الطلب بنجاح! انتقل للرحلة الجارية"
                _selectedTab.value = CaptainMainTab.ACTIVE_TRIP
                onSuccess?.invoke()
            } else {
                _userFeedbackMessage.value = "تعذر قبول الطلب (تأكد من حالة الاتصال وعدم وجود طلب نشط)"
            }
        }
    }

    fun rejectTask(orderId: String) {
        viewModelScope.launch {
            captainRepository.rejectTask(orderId)
            _userFeedbackMessage.value = "تم تخطي الطلب"
        }
    }

    fun updateActiveTaskStatus(orderId: String, newStatus: OrderStatus, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val success = captainRepository.updateTaskStatus(orderId, newStatus)
            if (success) {
                when (newStatus) {
                    OrderStatus.PICKED_UP -> _userFeedbackMessage.value = "تم تأكيد استلام الطلب من المطعم"
                    OrderStatus.ON_THE_WAY -> _userFeedbackMessage.value = "أنت الآن في الطريق إلى العميل"
                    OrderStatus.DELIVERED -> {
                        _userFeedbackMessage.value = "تم تسليم الطلب بنجاح وإيداع الأرباح في محفظتك"
                        _selectedTab.value = CaptainMainTab.HOME
                    }
                    else -> {}
                }
                onComplete?.invoke()
            } else {
                _userFeedbackMessage.value = "لا يمكن الانتقال لهذه المرحلة مباشرة"
            }
        }
    }

    fun requestPayout(amountEgp: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = captainRepository.requestPayout(amountEgp)
            if (success) {
                _userFeedbackMessage.value = "تم إرسال طلب تحويل $amountEgp ج.م بنجاح"
            } else {
                _userFeedbackMessage.value = "الرصيد المتاح غير كافٍ لتحويل هذا المبلغ"
            }
            onResult(success)
        }
    }

    fun selectHistoryTask(task: DeliveryTask?) {
        _selectedHistoryTask.value = task
    }

    fun setNotificationDialogVisible(visible: Boolean) {
        _showNotificationDialog.value = visible
    }

    fun setPayoutDialogVisible(visible: Boolean) {
        _payoutDialogState.value = visible
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            captainRepository.markNotificationAsRead(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            captainRepository.clearAllNotifications()
        }
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }
}
