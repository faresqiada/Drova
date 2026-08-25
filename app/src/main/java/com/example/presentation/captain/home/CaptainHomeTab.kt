package com.example.presentation.captain.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.CaptainMode
import com.example.domain.model.DeliveryTask
import com.example.domain.model.OrderStatus
import com.example.domain.model.PaymentMethod
import com.example.presentation.captain.CaptainMainTab
import com.example.presentation.captain.CaptainOrderOfferPolicy
import com.example.presentation.captain.CaptainViewModel
import com.example.ui.theme.*

@Composable
fun CaptainHomeTab(
    captainViewModel: CaptainViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val currentUser by captainViewModel.currentUser.collectAsState()
    val isOnline by captainViewModel.isOnline.collectAsState()
    val captainMode by captainViewModel.captainMode.collectAsState()
    val earnings by captainViewModel.earnings.collectAsState()
    val availableTasks by captainViewModel.availableTasks.collectAsState()
    val activeTask by captainViewModel.activeTask.collectAsState()
    val inYourWayTask = CaptainOrderOfferPolicy.findOrderInYourWay(activeTask, availableTasks)
    var ignoredInYourWayOrderId by remember { mutableStateOf<String?>(null) }
    var showInYourWayDetails by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("captain_home_tab")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Online / Offline Status Banner & Mode Quick Switch
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isOnline) DrovaSurface else DrovaSurfaceVariant,
                border = BorderStroke(1.5.dp, if (isOnline) DrovaTurquoise else DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) DrovaSuccess else DrovaTextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isOnline) (if (isAr) "أنت متصل (جاهز لاستقبال الطلبات)" else "You are Online")
                                    else (if (isAr) "أنت غير متصل حالياً" else "You are Offline"),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOnline) DrovaTextPrimary else DrovaTextSecondary
                                    )
                                )
                                Text(
                                    text = if (isOnline) (if (isAr) "نظام العمل: ${captainMode.titleAr}" else "Mode: ${captainMode.titleEn}")
                                    else (if (isAr) "قم بالاتصال لبدء استقبال الطلبات" else "Go online to start receiving orders"),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextSecondary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Big Prominent Online Toggle Switch
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { captainViewModel.toggleOnline(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DrovaTurquoise,
                                uncheckedThumbColor = DrovaTextMuted,
                                uncheckedTrackColor = DrovaBorder
                            ),
                            modifier = Modifier.testTag("captain_online_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Selection Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "تبديل نظام العمل:" else "Switch Mode:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DrovaTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (captainMode == CaptainMode.SHIFT_MODE) DrovaTurquoise else DrovaSurfaceVariant,
                                border = BorderStroke(1.dp, if (captainMode == CaptainMode.SHIFT_MODE) DrovaTurquoise else DrovaBorder),
                                modifier = Modifier
                                    .clickable { captainViewModel.setCaptainMode(CaptainMode.SHIFT_MODE) }
                                    .testTag("mode_chip_shift")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = if (captainMode == CaptainMode.SHIFT_MODE) Color.White else DrovaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "نظام الوردية (Shift)" else "Shift Mode",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (captainMode == CaptainMode.SHIFT_MODE) Color.White else DrovaTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (captainMode == CaptainMode.FREE_MODE) DrovaTurquoise else DrovaSurfaceVariant,
                                border = BorderStroke(1.dp, if (captainMode == CaptainMode.FREE_MODE) DrovaTurquoise else DrovaBorder),
                                modifier = Modifier
                                    .clickable { captainViewModel.setCaptainMode(CaptainMode.FREE_MODE) }
                                    .testTag("mode_chip_free")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Moped,
                                        contentDescription = null,
                                        tint = if (captainMode == CaptainMode.FREE_MODE) Color.White else DrovaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "العمل الحر (Free)" else "Free Mode",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (captainMode == CaptainMode.FREE_MODE) Color.White else DrovaTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Today's Earnings Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("captain_earnings_summary_card"),
                shape = RoundedCornerShape(16.dp),
                color = DrovaCharcoal
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "صافي أرباح اليوم" else "Today Net Earnings",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${earnings.todayNetEarningsEgp} ج.م",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaCyanAccent,
                                    fontSize = 26.sp
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isAr) "الرصيد المتاح بالمحفظة" else "Available Balance",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${earnings.walletBalanceEgp} ج.م",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = DrovaCyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${earnings.todayDeliveriesCount} ${if (isAr) "طلبات مكتملة اليوم" else "trips completed"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = DrovaWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "معدل القبول: ${earnings.acceptanceRatePercent}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Active Delivery Alert Banner (if currently in progress)
        if (activeTask != null) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { captainViewModel.selectTab(CaptainMainTab.ACTIVE_TRIP) }
                        .testTag("captain_active_trip_alert_banner"),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaTurquoiseLight,
                    border = BorderStroke(1.5.dp, DrovaTurquoise)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DrovaTurquoise),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsRun,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAr) "رحلة جارية الآن (${activeTask!!.orderNumber})" else "Active Trip (${activeTask!!.orderNumber})",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Text(
                                    text = "${activeTask!!.restaurantNameAr} ← ${activeTask!!.customerAddressAr}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary),
                                    maxLines = 1
                                )
                            }
                        }

                        Button(
                            onClick = { captainViewModel.selectTab(CaptainMainTab.ACTIVE_TRIP) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isAr) "متابعة" else "Open",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // 4. Non-blocking "Order in your way" notification.
        if (activeTask != null && inYourWayTask != null && ignoredInYourWayOrderId != inYourWayTask.orderId) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("captain_order_in_your_way_banner"),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaWarningContainer,
                    border = BorderStroke(1.5.dp, DrovaWarning)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NearMe, contentDescription = null, tint = DrovaWarningText)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "طلب في سكتك" else "Order in your way",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = DrovaWarningText
                            )
                        }
                        Text(
                            text = if (isAr)
                                "يوجد طلب مناسب بالقرب من مسارك الحالي. الطلب الحالي مستمر ولن يتم تغييره."
                            else
                                "A suitable order is near your current route. Your active order remains unchanged.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DrovaTextPrimary
                        )
                        Text(
                            text = "${inYourWayTask.restaurantNameAr} → ${inYourWayTask.customerAddressAr} • +${inYourWayTask.estimatedEarningsEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = DrovaTextPrimary,
                            maxLines = 2
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { ignoredInYourWayOrderId = inYourWayTask.orderId },
                                modifier = Modifier.weight(1f).testTag("captain_order_in_your_way_ignore"),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(if (isAr) "تجاهل" else "Ignore")
                            }
                            Button(
                                onClick = { showInYourWayDetails = true },
                                modifier = Modifier.weight(1f).testTag("captain_order_in_your_way_details"),
                                colors = ButtonDefaults.buttonColors(containerColor = DrovaWarning),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(if (isAr) "التفاصيل" else "Details", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 5. Incoming Delivery Requests Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "طلبات التوصيل المتاحة بالقرب منك" else "Available Delivery Requests",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaCharcoal
                    )
                )

                if (availableTasks.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DrovaTurquoiseLight
                    ) {
                        Text(
                            text = "${availableTasks.size} ${if (isAr) "طلبات متاحة" else "available"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTurquoiseHover
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 5. Incoming Delivery Requests List / Empty State
        if (!isOnline) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaSurfaceVariant,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = DrovaTextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isAr) "أنت غير متصل لاستقبال الطلبات" else "You are offline",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) "اضغط على زر الاتصال بالأعلى لبدء استلام إشعارات التوصيل القريبة" else "Toggle online status above to start receiving trips",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { captainViewModel.toggleOnline(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_go_online_prompt")
                        ) {
                            Text(
                                text = if (isAr) "الاتصال الآن" else "Go Online Now",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        } else if (availableTasks.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = DrovaTurquoise,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isAr) "جاري البحث عن طلبات توصيل في منطقتك..." else "Searching for orders in your zone...",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) "ستصلك إشعارات فورية فور تجهيز المطاعم لطلبات جديدة" else "You'll receive alerts as soon as orders are ready",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                    }
                }
            }
        } else {
            items(availableTasks, key = { it.orderId }) { task ->
                DeliveryRequestCard(
                    task = task,
                    onAccept = { captainViewModel.acceptTask(task.orderId) },
                    onReject = { captainViewModel.rejectTask(task.orderId) }
                )
            }
        }
    }

    if (showInYourWayDetails && inYourWayTask != null) {
        AlertDialog(
            onDismissRequest = { showInYourWayDetails = false },
            title = { Text(if (isAr) "تفاصيل طلب في سكتك" else "Order in your way details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${inYourWayTask.restaurantNameAr} → ${inYourWayTask.customerAddressAr}")
                    Text(inYourWayTask.itemsSummary)
                    Text("+${inYourWayTask.estimatedEarningsEgp} ج.م • ${inYourWayTask.estimatedTimeMin} دقيقة")
                    Text(
                        if (isAr) "يمكنك قبول الطلب من القائمة لاحقًا. لن يتغير الطلب الحالي تلقائيًا."
                        else "You can accept it from the list later. The active order will never change automatically."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInYourWayDetails = false }) {
                    Text(if (isAr) "إغلاق" else "Close")
                }
            }
        )
    }
}

@Composable
private fun DeliveryRequestCard(
    task: DeliveryTask,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("delivery_request_card_${task.orderId}"),
        shape = RoundedCornerShape(14.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Restaurant Name + Total Expected Earning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DrovaTurquoiseLight
                        ) {
                            Text(
                                text = task.orderNumber,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = task.restaurantNameAr,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = task.itemsSummary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+${task.estimatedEarningsEgp} ج.م",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaTurquoiseHover,
                            fontSize = 20.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (task.bonusEgp > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DrovaSuccessContainer
                            ) {
                                Text(
                                    text = "+${task.bonusEgp} حافز",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DrovaSuccessText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Route Details (Pickup & Dropoff addresses + Distances + ETA)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = DrovaCharcoal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الاستلام: ${task.restaurantAddressAr}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = DrovaTurquoise,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "التسليم: ${task.customerAddressAr} (${task.customerName})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row: Pickup Km, Dropoff Km, Total Time, Payment Method
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "🚗 مسافة: ${task.pickupDistanceKm + task.dropoffDistanceKm} كم",
                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                    )
                    Text(
                        text = "⏱️ الوقت: ${task.estimatedTimeMin} دقيقة",
                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                    )
                }

                Text(
                    text = if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) "💵 كاش (${task.orderTotalEgp} ج.م)" else "💳 دفع إلكتروني",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) DrovaWarningText else DrovaSuccessText
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Reject vs Accept
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_reject_${task.orderId}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Text(
                        text = if (isAr) "رفض" else "Decline",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = DrovaTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .weight(2f)
                        .height(46.dp)
                        .testTag("btn_accept_${task.orderId}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "قبول الطلب" else "Accept Order",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
