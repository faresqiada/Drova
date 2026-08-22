package com.example.presentation.captain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.designsystem.*
import com.example.domain.model.UserRole
import com.example.presentation.captain.active.CaptainActiveTripTab
import com.example.presentation.captain.home.CaptainHomeTab
import com.example.presentation.captain.modes.CaptainModesTab
import com.example.presentation.captain.notifications.CaptainNotificationDialog
import com.example.presentation.captain.profile.CaptainProfileTab
import com.example.presentation.captain.wallet.CaptainWalletTab
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptainDashboardScreen(
    onRoleSwitch: (UserRole) -> Unit,
    onLogout: () -> Unit,
    captainViewModel: CaptainViewModel = viewModel()
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val currentUser by captainViewModel.currentUser.collectAsState()
    val isOnline by captainViewModel.isOnline.collectAsState()
    val selectedTab by captainViewModel.selectedTab.collectAsState()
    val activeTask by captainViewModel.activeTask.collectAsState()
    val pendingRequestsCount by captainViewModel.pendingRequestsCount.collectAsState()
    val unreadNotifCount by captainViewModel.unreadNotificationsCount.collectAsState()
    val notifications by captainViewModel.notifications.collectAsState()
    val showNotifDialog by captainViewModel.showNotificationDialog.collectAsState()
    val feedbackMessage by captainViewModel.userFeedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            captainViewModel.clearFeedbackMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("captain_dashboard_screen"),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DrovaMark(size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAr) "كابتن DROVA" else "DROVA Captain",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaCharcoal
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) DrovaSuccess else DrovaTextMuted)
                                )
                            }
                            Text(
                                text = currentUser?.fullName ?: (if (isAr) "محمود عادل" else "Mahmoud Adel"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Notifications Bell with Badge
                    IconButton(
                        onClick = { captainViewModel.setNotificationDialogVisible(true) },
                        modifier = Modifier.testTag("btn_captain_notifications")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        containerColor = DrovaTurquoise,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotifCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (unreadNotifCount > 0) Icons.Default.NotificationsActive else Icons.Outlined.Notifications,
                                contentDescription = "الإشعارات",
                                tint = DrovaCharcoal
                            )
                        }
                    }

                    // Language Switcher
                    IconButton(
                        onClick = { DrovaLanguageManager.toggleLanguage() },
                        modifier = Modifier.testTag("btn_language_toggle_captain")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = DrovaSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isAr) "EN" else "ع",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaCharcoal
                                    )
                                )
                            }
                        }
                    }

                    // Logout Icon
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("btn_logout_topbar_captain")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "خروج",
                            tint = DrovaTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DrovaSurface,
                    scrolledContainerColor = DrovaSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DrovaSurface,
                contentColor = DrovaTurquoise,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("captain_bottom_nav")
            ) {
                CaptainMainTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val badgeCount = when (tab) {
                        CaptainMainTab.HOME -> if (isOnline) pendingRequestsCount else 0
                        CaptainMainTab.ACTIVE_TRIP -> if (activeTask != null) 1 else 0
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { captainViewModel.selectTab(tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (badgeCount > 0) {
                                        Badge(
                                            containerColor = if (tab == CaptainMainTab.ACTIVE_TRIP) DrovaWarning else DrovaTurquoise,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (tab == CaptainMainTab.ACTIVE_TRIP) "!" else "$badgeCount",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = getCaptainTabIcon(tab, isSelected),
                                    contentDescription = if (isAr) tab.titleAr else tab.titleEn
                                )
                            }
                        },
                        label = {
                            Text(
                                text = if (isAr) tab.titleAr else tab.titleEn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DrovaTurquoise,
                            selectedTextColor = DrovaTurquoise,
                            indicatorColor = DrovaTurquoiseLight,
                            unselectedIconColor = DrovaTextSecondary,
                            unselectedTextColor = DrovaTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_captain_${tab.name}")
                    )
                }
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                CaptainMainTab.HOME -> {
                    CaptainHomeTab(captainViewModel = captainViewModel)
                }
                CaptainMainTab.ACTIVE_TRIP -> {
                    CaptainActiveTripTab(captainViewModel = captainViewModel)
                }
                CaptainMainTab.MODES -> {
                    CaptainModesTab(captainViewModel = captainViewModel)
                }
                CaptainMainTab.WALLET -> {
                    CaptainWalletTab(captainViewModel = captainViewModel)
                }
                CaptainMainTab.PROFILE -> {
                    CaptainProfileTab(
                        captainViewModel = captainViewModel,
                        onRoleSwitch = onRoleSwitch,
                        onLogout = onLogout
                    )
                }
            }
        }
    }

    // In-App Notification Center
    if (showNotifDialog) {
        CaptainNotificationDialog(
            notifications = notifications,
            onDismiss = { captainViewModel.setNotificationDialogVisible(false) },
            onMarkRead = { captainViewModel.markNotificationAsRead(it) },
            onClearAll = { captainViewModel.clearAllNotifications() }
        )
    }
}

private fun getCaptainTabIcon(tab: CaptainMainTab, isSelected: Boolean): ImageVector {
    return when (tab) {
        CaptainMainTab.HOME -> if (isSelected) Icons.Default.Home else Icons.Outlined.Home
        CaptainMainTab.ACTIVE_TRIP -> if (isSelected) Icons.Default.DirectionsRun else Icons.Outlined.DirectionsRun
        CaptainMainTab.MODES -> if (isSelected) Icons.Default.Speed else Icons.Outlined.Speed
        CaptainMainTab.WALLET -> if (isSelected) Icons.Default.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet
        CaptainMainTab.PROFILE -> if (isSelected) Icons.Default.Person else Icons.Outlined.Person
    }
}
