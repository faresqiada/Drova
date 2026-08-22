package com.example.presentation.customer.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.OrderTimelineEvent
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
    order: Order,
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    var showCancelDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_tracking_screen"),
        topBar = {
            DrovaTopBar(
                title = "${if (isAr) "تتبع الطلب" else "Track Order"} #${order.orderNumber}",
                onBackClick = { viewModel.navigateBack() }
            )
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Active State Card
            item {
                HeroOrderStatusCard(order = order)
            }

            // Cancellation or Rejection Reason Banner
            if (order.status == OrderStatus.CANCELLED && !order.cancellationReason.isNullOrBlank()) {
                item {
                    DrovaSurface(
                        modifier = Modifier.fillMaxWidth().testTag("cancellation_reason_banner"),
                        style = DrovaSurfaceStyle.FLAT,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                tint = DrovaError,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "سبب إلغاء الطلب:" else "Cancellation Reason:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DrovaError)
                                )
                                Text(
                                    text = order.cancellationReason,
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary, fontSize = 12.sp)
                                )
                            }
                        }
                    }
                }
            }

            if (order.status == OrderStatus.REJECTED && !order.rejectionReason.isNullOrBlank()) {
                item {
                    DrovaSurface(
                        modifier = Modifier.fillMaxWidth().testTag("rejection_reason_banner"),
                        style = DrovaSurfaceStyle.FLAT,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = DrovaWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "سبب رفض المطعم للطلب:" else "Restaurant Rejection Reason:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DrovaWarning)
                                )
                                Text(
                                    text = order.rejectionReason,
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary, fontSize = 12.sp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Interactive 9-Stage Progress Timeline
            if (!order.status.isFailure) {
                item {
                    DrovaSectionContainer(
                        title = if (isAr) "مراحل تنفيذ الطلب (9 مراحل)" else "Order Lifecycle (9 Stages)",
                        style = DrovaSurfaceStyle.FLAT
                    ) {
                        NineStageOrderStepper(currentStatus = order.status)
                    }
                }
            }

            // 3. Captain Assigned Info Card (if assigned or beyond)
            if (order.status.stepIndex >= OrderStatus.CAPTAIN_ASSIGNED.stepIndex && !order.status.isTerminal) {
                item {
                    CaptainInfoCard(
                        captainName = order.captainName ?: "محمود عادل (كابتن DROVA)",
                        captainPhone = order.captainPhone ?: "+201198765432",
                        vehicleType = order.captainVehicleType ?: "سكوتر هوندا أبيض",
                        rating = order.captainRating
                    )
                }
            }

            // 4. Customer Receipt Confirmation (When Delivered)
            if (order.status == OrderStatus.DELIVERED) {
                item {
                    DrovaSurface(
                        modifier = Modifier.fillMaxWidth().testTag("confirm_delivery_card"),
                        style = DrovaSurfaceStyle.TONAL,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = DrovaSuccess,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "تم تسليم الطلب بنجاح" else "Order Delivered",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DrovaDeep)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAr) "يرجى تأكيد استلام وجبتك لإغلاق الحساب وتقييم التجربة." else "Please confirm receipt to complete the order.",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary, fontSize = 11.sp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            DrovaPrimaryButton(
                                text = if (isAr) "تأكيد استلام الطلب وإغلاق الحساب ✓" else "Confirm Receipt & Complete ✓",
                                onClick = { viewModel.advanceOrderSimulation(order.id) },
                                testTag = "confirm_delivered_btn"
                            )
                        }
                    }
                }
            }

            // 5. Restaurant & Delivery Details Card
            item {
                DrovaSectionContainer(
                    title = if (isAr) "تفاصيل المطعم والتوصيل" else "Restaurant & Delivery Info",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DrovaTurquoiseLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = DrovaPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = order.restaurantNameAr,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = order.restaurantAddressAr,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DrovaSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = DrovaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "عنوان التسليم" else "Dropoff Address",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = order.deliveryAddressAr,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 6. Order Items & Invoice Summary
            item {
                DrovaSectionContainer(
                    title = if (isAr) "الوجبات المطلوبة (${order.items.size})" else "Order Items (${order.items.size})",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.quantity} × ${item.nameAr}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "${item.unitPriceEgp * item.quantity} ج.م",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "الإجمالي (${order.paymentMethod.titleAr})" else "Total (${order.paymentMethod.titleEn})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaDeep,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "${order.totalEgp} ج.م",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaPrimary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }

            // 7. Order Timeline History
            if (order.timeline.isNotEmpty()) {
                item {
                    DrovaSectionContainer(
                        title = if (isAr) "سجل الأحداث والوقت" else "Order Timeline Events",
                        style = DrovaSurfaceStyle.FLAT
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            order.timeline.forEach { event ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(DrovaPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = event.titleAr,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DrovaTextPrimary,
                                                fontSize = 12.sp
                                            )
                                        )
                                        if (event.noteAr.isNotBlank()) {
                                            Text(
                                                text = event.noteAr,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = DrovaTextSecondary,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = event.formattedTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DrovaTextMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 8. Real-time Lifecycle Simulation Controller Card (When active)
            if (!order.status.isTerminal) {
                item {
                    DrovaSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lifecycle_simulation_card"),
                        style = DrovaSurfaceStyle.TONAL,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = DrovaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "محاكي دورة حياة الطلب الفورية" else "Live Lifecycle Simulator",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaDeep,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isAr)
                                    "يمكنك تقديم الطلب للمرحلة التالية لمشاهدة تحديث الحالة الفوري وانتقال الكابتن وتحديث الخريطة."
                                else
                                    "Advance the order to the next stage to preview real-time lifecycle transitions.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 11.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DrovaPrimaryButton(
                                text = if (isAr) "تقديم الطلب للمرحلة التالية ⚡" else "Advance to Next Stage ⚡",
                                onClick = { viewModel.advanceOrderSimulation(order.id) },
                                testTag = "tracking_advance_stage_btn"
                            )
                        }
                    }
                }
            }

            // 9. Cancel Order Option (if early in lifecycle)
            if (order.status.stepIndex <= OrderStatus.PREPARING.stepIndex && !order.status.isTerminal) {
                item {
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tracking_cancel_order_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DrovaError),
                        border = BorderStroke(1.dp, DrovaError.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isAr) "إلغاء الطلب" else "Cancel Order",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaError
                            )
                        )
                    }
                }
            }
        }
    }

    // Cancellation Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = if (isAr) "تأكيد إلغاء الطلب" else "Confirm Order Cancellation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = if (isAr) "هل أنت متأكد من رغبتك في إلغاء هذا الطلب؟" else "Are you sure you want to cancel this order?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(order.id)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaError),
                    modifier = Modifier.testTag("confirm_cancel_dialog_btn")
                ) {
                    Text(text = if (isAr) "نعم، إلغاء" else "Yes, Cancel", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(text = if (isAr) "تراجع" else "Back")
                }
            }
        )
    }
}

@Composable
private fun HeroOrderStatusCard(order: Order) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_order_status_card"),
        style = if (order.status.isFailure) DrovaSurfaceStyle.FLAT else DrovaSurfaceStyle.HERO_DEEP,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (order.status.isFailure) DrovaError.copy(alpha = 0.15f) else DrovaPrimary.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = if (order.status.isFailure)
                            (if (isAr) "طلب متوقف" else "Terminated")
                        else
                            "${if (isAr) "المرحلة" else "Stage"} ${order.status.stepIndex} / 9",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (order.status.isFailure) DrovaError else DrovaCyanAccent,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (order.estimatedArrivalMin > 0 && !order.status.isTerminal) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${if (isAr) "الوصول المتوقع:" else "ETA:"} ${order.estimatedArrivalMin} ${if (isAr) "دقيقة" else "mins"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = order.status.titleAr,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (order.status.isFailure) DrovaTextPrimary else Color.White,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = order.status.descriptionAr,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (order.status.isFailure) DrovaTextSecondary else Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun NineStageOrderStepper(currentStatus: OrderStatus) {
    val stages = listOf(
        OrderStatus.CREATED,
        OrderStatus.RESTAURANT_CONFIRMED,
        OrderStatus.PREPARING,
        OrderStatus.READY_FOR_PICKUP,
        OrderStatus.CAPTAIN_ASSIGNED,
        OrderStatus.PICKED_UP,
        OrderStatus.ON_THE_WAY,
        OrderStatus.DELIVERED,
        OrderStatus.COMPLETED
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stages.forEach { stage ->
            val isPassed = currentStatus.stepIndex > stage.stepIndex
            val isCurrent = currentStatus == stage

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPassed -> DrovaPrimary
                                isCurrent -> DrovaCyanAccent
                                else -> DrovaSurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "${stage.stepIndex}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) DrovaDeep else DrovaTextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stage.titleAr,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isCurrent) FontWeight.Black else if (isPassed) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) DrovaPrimary else if (isPassed) DrovaTextPrimary else DrovaTextMuted,
                            fontSize = 13.sp
                        )
                    )
                    if (isCurrent) {
                        Text(
                            text = stage.descriptionAr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DrovaTurquoiseLight
                    ) {
                        Text(
                            text = "المرحلة الحالية",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTurquoiseHover,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptainInfoCard(
    captainName: String,
    captainPhone: String,
    vehicleType: String = "سكوتر هوندا أبيض",
    rating: Double = 4.9
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("captain_info_card"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DrovaTurquoiseLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = null,
                        tint = DrovaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = captainName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$vehicleType • $rating ★",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = DrovaTurquoiseLight,
                border = BorderStroke(1.dp, DrovaTurquoise.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = DrovaTurquoiseHover,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "اتصال" else "Call",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTurquoiseHover,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
