package com.example.presentation.restaurant.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.Restaurant
import com.example.presentation.restaurant.*
import com.example.presentation.restaurant.orders.RejectOrderDialog
import com.example.presentation.restaurant.orders.RestaurantOrderDetailDialog
import com.example.ui.theme.*

@Composable
fun RestaurantDashboardTab(
    restaurantViewModel: RestaurantViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val restaurant by restaurantViewModel.restaurantData.collectAsState()
    val activeAlert by restaurantViewModel.activeAlert.collectAsState()
    var showDeliveryRequestDialog by remember { mutableStateOf(false) }
    var selectedDeliveryArea by remember { mutableStateOf("6 أكتوبر") }
    val deliveryAreas = listOf("6 أكتوبر", "الشيخ زايد", "حدائق أكتوبر")

    val todayOrdersCount by restaurantViewModel.todayOrdersCount.collectAsState()
    val todaySalesEgp by restaurantViewModel.todaySalesEgp.collectAsState()
    val netSettlementBalanceEgp by restaurantViewModel.netSettlementBalanceEgp.collectAsState()
    val activeOrdersCount by restaurantViewModel.activeOrdersCount.collectAsState()
    val preparingOrdersCount by restaurantViewModel.preparingOrdersCount.collectAsState()
    val readyOrdersCount by restaurantViewModel.readyOrdersCount.collectAsState()
    val completedOrdersCount by restaurantViewModel.completedOrdersCount.collectAsState()
    val pendingActionsCount by restaurantViewModel.pendingActionsCount.collectAsState()

    val restaurantOrders by restaurantViewModel.restaurantOrders.collectAsState()
    val pendingOrders = restaurantOrders.filter { it.status == OrderStatus.CREATED || it.status == OrderStatus.RESTAURANT_CONFIRMED }
    val inProgressOrders = restaurantOrders.filter { it.status == OrderStatus.PREPARING || it.status == OrderStatus.READY_FOR_PICKUP }
    val selectedOrderDetail by restaurantViewModel.selectedOrderDetail.collectAsState()

    var orderToReject by remember { mutableStateOf<Order?>(null) }

    // Dialog for full order details
    if (selectedOrderDetail != null) {
        RestaurantOrderDetailDialog(
            order = selectedOrderDetail!!,
            restaurantViewModel = restaurantViewModel,
            onDismiss = { restaurantViewModel.closeOrderDetail() }
        )
    }

    // Dialog for rejecting order with mandatory reason
    if (orderToReject != null) {
        RejectOrderDialog(
            order = orderToReject!!,
            onConfirmReject = { reason ->
                restaurantViewModel.rejectOrder(orderToReject!!.id, reason)
                orderToReject = null
            },
            onDismiss = { orderToReject = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_dashboard_tab"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Restaurant Status & Live Toggle Card
        item {
            StoreStatusHeroCard(
                restaurant = restaurant,
                isOpen = restaurant?.isOpen == true,
                onToggleStatus = { restaurantViewModel.toggleRestaurantStatus(it) }
            )
        }

        // 2. Interactive Alert Banner (if active)
        if (activeAlert != null) {
            item {
                RestaurantAlertBanner(
                    alert = activeAlert!!,
                    onDismiss = { restaurantViewModel.dismissAlert() },
                    onAction = {
                        val orderId = activeAlert?.orderId
                        if (orderId != null) {
                            val order = restaurantOrders.find { it.id == orderId }
                            if (order != null) {
                                restaurantViewModel.openOrderDetail(order)
                            }
                        }
                    }
                )
            }
        }

        // 3. Operational KPI Metrics Section (Today's orders, Active orders, Today's sales, Pending settlement, status breakdown)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "مؤشرات التشغيل والأرباح" else "Operations & Sales KPIs",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaCharcoal
                    )
                )

                // Row 1: Today's Sales & Pending Settlement
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricCard(
                        title = if (isAr) "مبيعات اليوم" else "Today's Sales",
                        value = "${"%,.0f".format(todaySalesEgp)} ج.م",
                        subtitle = if (isAr) "$todayOrdersCount طلبات مسجلة" else "$todayOrdersCount orders",
                        icon = Icons.Default.Payments,
                        containerColor = DrovaCharcoal,
                        contentColor = DrovaCyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricCard(
                        title = if (isAr) "التسوية المستحقة" else "Pending Settlement",
                        value = "${"%,.0f".format(netSettlementBalanceEgp)} ج.م",
                        subtitle = if (isAr) "تحويل بنكي أسبوعي" else "Weekly bank transfer",
                        icon = Icons.Default.AccountBalance,
                        containerColor = DrovaTurquoise,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Status Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniKpiCard(
                        title = if (isAr) "طلب معلق" else "Pending",
                        count = "$pendingActionsCount",
                        color = if (pendingActionsCount > 0) DrovaWarning else DrovaTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    MiniKpiCard(
                        title = if (isAr) "في المطبخ" else "In Kitchen",
                        count = "$preparingOrdersCount",
                        color = DrovaTurquoise,
                        modifier = Modifier.weight(1f)
                    )
                    MiniKpiCard(
                        title = if (isAr) "جاهز للاستلام" else "Ready",
                        count = "$readyOrdersCount",
                        color = DrovaCyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MiniKpiCard(
                        title = if (isAr) "نشطة إجمالاً" else "Active",
                        count = "$activeOrdersCount",
                        color = DrovaCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Quick Actions (Orders, Menu, Finance, Settings)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "الوصول السريع" else "Quick Actions",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaCharcoal
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = if (isAr) "الطلبات" else "Orders",
                        icon = Icons.Default.RestaurantMenu,
                        onClick = { restaurantViewModel.selectTab(RestaurantMainTab.ORDERS) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = if (isAr) "القائمة" else "Menu",
                        icon = Icons.Default.MenuBook,
                        onClick = { restaurantViewModel.selectTab(RestaurantMainTab.MENU) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = if (isAr) "المالية" else "Finance",
                        icon = Icons.Default.AccountBalanceWallet,
                        onClick = { restaurantViewModel.selectTab(RestaurantMainTab.FINANCE) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = if (isAr) "إعدادات المطعم" else "Settings",
                        icon = Icons.Default.Storefront,
                        onClick = { restaurantViewModel.selectTab(RestaurantMainTab.PROFILE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Delivery-only service
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("restaurant_delivery_only_service"),
                shape = RoundedCornerShape(16.dp),
                color = DrovaTurquoiseLight,
                border = BorderStroke(1.dp, DrovaTurquoise.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = DrovaTurquoise,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "اطلب دليفري" else "Request delivery",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DrovaCharcoal
                        )
                    }
                    Text(
                        text = if (isAr)
                            "خدمة مستقلة لطلب التوصيل فقط. حدد نطاق التوصيل الخاص بمطعمك."
                        else
                            "A dedicated delivery-only service. Choose your restaurant delivery area.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DrovaTextSecondary
                    )
                    Text(
                        text = if (isAr) "النطاق المحدد: $selectedDeliveryArea" else "Selected area: $selectedDeliveryArea",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = DrovaTextPrimary
                    )
                    DrovaOutlinedButton(
                        text = if (isAr) "تحديد نطاق التوصيل" else "Choose delivery area",
                        onClick = { showDeliveryRequestDialog = true },
                        leadingIcon = Icons.Default.LocationOn,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "restaurant_delivery_area_button"
                    )
                }
            }
        }

        // 6. Orders Waiting for Confirmation & Preparation
        if (pendingOrders.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(DrovaWarning)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "طلبات بانتظار اتخاذ إجراء (${pendingOrders.size})" else "Orders Requiring Action (${pendingOrders.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                        }

                        TextButton(onClick = { restaurantViewModel.selectTab(RestaurantMainTab.ORDERS) }) {
                            Text(
                                text = if (isAr) "عرض الكل" else "View All",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover
                                )
                            )
                        }
                    }

                    pendingOrders.forEach { order ->
                        DashboardOrderActionCard(
                            order = order,
                            onConfirm = { restaurantViewModel.confirmOrder(order.id) },
                            onStartPrep = { restaurantViewModel.startPreparing(order.id) },
                            onReject = { orderToReject = order },
                            onClick = { restaurantViewModel.openOrderDetail(order) }
                        )
                    }
                }
            }
        }

        // 6. In Kitchen / Ready Quick Queue
        if (inProgressOrders.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "الطلبات قيد التحضير والتسليم (${inProgressOrders.size})" else "Orders In Kitchen & Handover (${inProgressOrders.size})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaCharcoal
                            )
                        )

                        TextButton(onClick = { restaurantViewModel.selectTab(RestaurantMainTab.ORDERS) }) {
                            Text(
                                text = if (isAr) "طابور المطبخ" else "Kitchen Queue",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover
                                )
                            )
                        }
                    }

                    inProgressOrders.forEach { order ->
                        DashboardActivePrepCard(
                            order = order,
                            onMarkReady = { restaurantViewModel.markReadyForPickup(order.id) },
                            onClick = { restaurantViewModel.openOrderDetail(order) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDeliveryRequestDialog) {
        AlertDialog(
            onDismissRequest = { showDeliveryRequestDialog = false },
            title = { Text(if (isAr) "نطاق خدمة اطلب دليفري" else "Delivery service area") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    deliveryAreas.forEach { area ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDeliveryArea = area },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDeliveryArea == area,
                                onClick = { selectedDeliveryArea = area }
                            )
                            Text(area, color = DrovaTextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeliveryRequestDialog = false }) {
                    Text(if (isAr) "حفظ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeliveryRequestDialog = false }) {
                    Text(if (isAr) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun StoreStatusHeroCard(
    restaurant: Restaurant?,
    isOpen: Boolean,
    onToggleStatus: (Boolean) -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, if (isOpen) DrovaTurquoise.copy(alpha = 0.4f) else DrovaBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isOpen) DrovaSuccess else DrovaError)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = restaurant?.nameAr ?: "شاورما الريم",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaTextPrimary
                            )
                        )
                        Text(
                            text = if (isOpen) (if (isAr) "متاح للطلب واستقبال الزبائن" else "Open & Accepting Orders")
                            else (if (isAr) "المطعم مغلق مؤقتاً" else "Store is Closed / Paused"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isOpen) DrovaSuccessText else DrovaErrorText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Switch(
                    checked = isOpen,
                    onCheckedChange = onToggleStatus,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = DrovaTurquoise,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = DrovaTextSecondary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("toggle_store_open_status")
                )
            }
        }
    }
}

@Composable
private fun RestaurantAlertBanner(
    alert: RestaurantAlert,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    val containerColor = when (alert.type) {
        AlertType.NEW_ORDER -> DrovaTurquoiseLight
        AlertType.SUCCESS -> DrovaSuccessContainer
        AlertType.WARNING -> DrovaWarningContainer
        AlertType.INFO -> DrovaSurfaceVariant
    }

    val textColor = when (alert.type) {
        AlertType.NEW_ORDER -> DrovaTurquoiseHover
        AlertType.SUCCESS -> DrovaSuccessText
        AlertType.WARNING -> DrovaWarningText
        AlertType.INFO -> DrovaTextPrimary
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction() }
            .testTag("restaurant_alert_banner"),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (alert.type) {
                    AlertType.NEW_ORDER -> Icons.Default.NotificationsActive
                    AlertType.SUCCESS -> Icons.Default.CheckCircle
                    AlertType.WARNING -> Icons.Default.Warning
                    AlertType.INFO -> Icons.Default.Info
                },
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAr) alert.titleAr else alert.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
                Text(
                    text = if (isAr) alert.messageAr else alert.messageEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DrovaTextPrimary.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = DrovaTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = contentColor,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun MiniKpiCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = color,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = DrovaTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DashboardOrderActionCard(
    order: Order,
    onConfirm: () -> Unit,
    onStartPrep: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("dashboard_pending_order_${order.id}"),
        shape = RoundedCornerShape(14.dp),
        color = DrovaSurface,
        border = BorderStroke(
            1.5.dp,
            if (order.status == OrderStatus.CREATED) DrovaWarning.copy(alpha = 0.8f) else DrovaTurquoise.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DrovaStatusBadge(status = order.status)
                }

                Text(
                    text = "${order.subtotalEgp} ج.م",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = DrovaTurquoiseHover
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${order.customerName} (${order.customerPhone}) • ${order.createdAtFormatted}",
                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Items summary
            Text(
                text = order.items.joinToString(" + ") { "${it.quantity}x ${it.nameAr}" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = DrovaTextPrimary
                ),
                maxLines = 2
            )

            if (order.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ملاحظة: ${order.specialInstructions}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DrovaWarningText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Strict contextual transitions for CREATED / RESTAURANT_CONFIRMED
            if (order.status == OrderStatus.CREATED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DrovaErrorContainer,
                            contentColor = DrovaErrorText
                        )
                    ) {
                        Text(
                            text = if (isAr) "رفض" else "Reject",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "تأكيد الطلب" else "Confirm",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            } else if (order.status == OrderStatus.RESTAURANT_CONFIRMED) {
                Button(
                    onClick = onStartPrep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                ) {
                    Icon(
                        imageVector = Icons.Default.SoupKitchen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "بدء التحضير بالمطبخ" else "Start Kitchen Prep",
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

@Composable
private fun DashboardActivePrepCard(
    order: Order,
    onMarkReady: () -> Unit,
    onClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DrovaStatusBadge(status = order.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = order.items.joinToString("، ") { "${it.quantity}x ${it.nameAr}" },
                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary),
                    maxLines = 1
                )
            }

            if (order.status == OrderStatus.PREPARING) {
                Button(
                    onClick = onMarkReady,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = if (isAr) "جاهز للاستلام" else "Ready",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DrovaTurquoiseLight
                ) {
                    Text(
                        text = if (isAr) "بانتظار الكابتن" else "Awaiting Captain",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DrovaTurquoiseHover,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DrovaTurquoise,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DrovaTextPrimary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
