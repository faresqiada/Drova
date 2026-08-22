package com.example.presentation.restaurant.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.designsystem.*
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.PaymentStatus
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.ui.theme.*

@Composable
fun RestaurantOrderDetailDialog(
    order: Order,
    restaurantViewModel: RestaurantViewModel,
    onDismiss: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    var showRejectDialog by remember { mutableStateOf(false) }

    if (showRejectDialog) {
        RejectOrderDialog(
            order = order,
            onConfirmReject = { reason ->
                restaurantViewModel.rejectOrder(order.id, reason)
                showRejectDialog = false
                onDismiss()
            },
            onDismiss = { showRejectDialog = false }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("restaurant_order_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = DrovaBackground,
            border = BorderStroke(1.dp, DrovaBorder)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    color = DrovaSurface,
                    border = BorderStroke(0.dp, Color.Transparent),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "تفاصيل الطلب ${order.orderNumber}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                DrovaStatusBadge(status = order.status)
                            }
                            Text(
                                text = "${order.createdAtFormatted} • ${order.paymentMethod.titleAr}",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("close_order_detail_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = DrovaTextPrimary
                            )
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Rejection Banner if rejected
                    if (order.status == OrderStatus.REJECTED && !order.rejectionReason.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DrovaErrorContainer,
                            border = BorderStroke(1.dp, DrovaError.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isAr) "تم رفض الطلب" else "Order Rejected",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = DrovaErrorText,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "سبب الرفض المسجل: ${order.rejectionReason}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaErrorText,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    // Cancellation Banner if cancelled
                    if (order.status == OrderStatus.CANCELLED && !order.cancellationReason.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DrovaErrorContainer,
                            border = BorderStroke(1.dp, DrovaError.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isAr) "تم إلغاء الطلب" else "Order Cancelled",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = DrovaErrorText,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "سبب الإلغاء: ${order.cancellationReason}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaErrorText,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    // Customer & Delivery Info Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DrovaSurface,
                        border = BorderStroke(1.dp, DrovaBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "بيانات العميل وعنوان التوصيل" else "Customer & Delivery Info",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = DrovaTurquoise,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${order.customerName} (${order.customerPhone})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DrovaTextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = DrovaTurquoise,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.deliveryAddressAr,
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                )
                            }

                            if (order.specialInstructions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DrovaWarningContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notes,
                                            contentDescription = null,
                                            tint = DrovaWarningText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ملاحظات العميل: ${order.specialInstructions}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = DrovaWarningText
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Assigned Captain Info (if assigned)
                    if (order.captainName != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DrovaSurface,
                            border = BorderStroke(1.dp, DrovaBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isAr) "كابتن التوصيل المعين" else "Assigned Delivery Captain",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaCharcoal
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(DrovaTurquoiseLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DirectionsBike,
                                                contentDescription = null,
                                                tint = DrovaTurquoiseHover,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = order.captainName ?: "",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = DrovaTextPrimary
                                                )
                                            )
                                            Text(
                                                text = order.captainPhone ?: "",
                                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DrovaSuccessContainer
                                    ) {
                                        Text(
                                            text = "متصل",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = DrovaSuccessText,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Order Items Breakdown Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DrovaSurface,
                        border = BorderStroke(1.dp, DrovaBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "قائمة الأصناف ومحتوى الوجبة" else "Order Items",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            order.items.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = DrovaSurfaceVariant,
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${item.quantity}x",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = DrovaTurquoiseHover
                                                    )
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = item.nameAr,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = DrovaTextPrimary
                                                )
                                            )
                                            Text(
                                                text = "سعر الوحدة: ${item.unitPriceEgp} ج.م",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = DrovaTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            )
                                            if (item.notes.isNotBlank()) {
                                                Text(
                                                    text = "تعديل: ${item.notes}",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = DrovaTextSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "${item.totalEgp} ج.م",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary
                                        )
                                    )
                                }

                                if (index < order.items.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    // Complete Financials Card (Subtotal, Delivery fee, Platform fee, Discount, Total, Payment method & status)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DrovaSurface,
                        border = BorderStroke(1.dp, DrovaBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "تفاصيل الفاتورة والدفع" else "Billing & Payment Breakdown",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            FinancialDetailRow(
                                label = if (isAr) "المجموع الفرعي للأصناف" else "Subtotal",
                                value = "${order.subtotalEgp} ج.م"
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            FinancialDetailRow(
                                label = if (isAr) "رسوم التوصيل" else "Delivery Fee",
                                value = "${order.deliveryFeeEgp} ج.م"
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            FinancialDetailRow(
                                label = if (isAr) "رسوم منصة DROVA" else "Platform Fee",
                                value = "${order.platformFeeEgp} ج.م"
                            )

                            if (order.discountEgp > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                FinancialDetailRow(
                                    label = if (isAr) "الخصم" else "Discount",
                                    value = "- ${order.discountEgp} ج.م",
                                    valueColor = DrovaSuccessText
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DrovaBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAr) "المبلغ الإجمالي:" else "Total Amount:",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Text(
                                    text = "${order.totalEgp} ج.م",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = DrovaTurquoiseHover,
                                        fontSize = 18.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "طريقة الدفع:",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                )
                                Text(
                                    text = order.paymentMethod.titleAr,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "حالة الدفع:",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (order.paymentStatus == PaymentStatus.PAID) DrovaSuccessContainer else DrovaWarningContainer
                                ) {
                                    Text(
                                        text = order.paymentStatus.titleAr,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (order.paymentStatus == PaymentStatus.PAID) DrovaSuccessText else DrovaWarningText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Complete Timeline Events List
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DrovaSurface,
                        border = BorderStroke(1.dp, DrovaBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "سجل مراحل الطلب (Timeline)" else "Order Timeline",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (order.timeline.isNotEmpty()) {
                                order.timeline.forEachIndexed { idx, event ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(DrovaTurquoise),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = event.titleAr,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = DrovaTextPrimary
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = event.formattedTime,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = DrovaTextSecondary,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                            if (event.noteAr.isNotBlank()) {
                                                Text(
                                                    text = event.noteAr,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = DrovaTextSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    if (idx < order.timeline.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 7.dp)
                                                .width(2.dp)
                                                .height(12.dp)
                                                .background(DrovaBorder)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "تم إنشاء الطلب بنجاح",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                )
                            }
                        }
                    }
                }

                // Sticky Action Bottom Bar (STRICT RESTAURANT CONTEXTUAL TRANSITIONS)
                Surface(
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        when (order.status) {
                            OrderStatus.CREATED -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { showRejectDialog = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DrovaErrorContainer,
                                            contentColor = DrovaErrorText
                                        )
                                    ) {
                                        Text(
                                            text = if (isAr) "رفض الطلب" else "Reject",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            restaurantViewModel.confirmOrder(order.id)
                                        },
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .height(46.dp),
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
                                            text = if (isAr) "تأكيد الطلب" else "Confirm Order",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            OrderStatus.RESTAURANT_CONFIRMED -> {
                                Button(
                                    onClick = {
                                        restaurantViewModel.startPreparing(order.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SoupKitchen,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAr) "بدء التحضير بالمطبخ" else "Start Kitchen Prep",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            OrderStatus.PREPARING -> {
                                DrovaPrimaryButton(
                                    text = if (isAr) "جاهز للاستلام والتسليم للكابتن" else "Mark Ready for Pickup",
                                    onClick = { restaurantViewModel.markReadyForPickup(order.id) },
                                    leadingIcon = Icons.Default.CheckCircle,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            OrderStatus.READY_FOR_PICKUP -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = DrovaTurquoiseLight
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeliveryDining,
                                            contentDescription = null,
                                            tint = DrovaTurquoiseHover
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isAr) "جاهز للاستلام - بانتظار استلام الكابتن" else "Ready - Awaiting Captain Handover",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DrovaTurquoiseHover
                                            )
                                        )
                                    }
                                }
                            }

                            OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = DrovaSurfaceVariant
                                ) {
                                    Text(
                                        text = if (isAr) "الطلب حالياً مع كابتن التوصيل في الطريق للعميل" else "Order is in transit with delivery captain",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DrovaTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            else -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (order.status.isFailure) DrovaErrorContainer else DrovaSuccessContainer
                                ) {
                                    Text(
                                        text = if (order.status.isFailure)
                                            (if (isAr) "تم إنهاء هذا الطلب (ملغي أو مرفوض)" else "Order Cancelled / Rejected")
                                        else
                                            (if (isAr) "تم تسليم هذا الطلب بنجاح" else "Order Delivered Successfully"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (order.status.isFailure) DrovaErrorText else DrovaSuccessText,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialDetailRow(
    label: String,
    value: String,
    valueColor: Color = DrovaTextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        )
    }
}
