package com.example.presentation.restaurant.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.domain.model.PaymentStatus
import com.example.presentation.restaurant.OrderQueueFilter
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.ui.theme.*

@Composable
fun RestaurantOrdersTab(
    restaurantViewModel: RestaurantViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val filteredOrders by restaurantViewModel.filteredOrders.collectAsState()
    val allRestaurantOrders by restaurantViewModel.restaurantOrders.collectAsState()
    val selectedFilter by restaurantViewModel.selectedQueueFilter.collectAsState()
    val searchQuery by restaurantViewModel.orderSearchQuery.collectAsState()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_orders_tab")
    ) {
        // Search Bar & Filter Chips Header
        Surface(
            color = DrovaSurface,
            border = BorderStroke(1.dp, DrovaBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { restaurantViewModel.updateOrderSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = if (isAr) "بحث برقم الطلب، اسم العميل، أو الصنف..." else "Search by order #, customer, item...",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = DrovaTurquoise
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { restaurantViewModel.updateOrderSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "مسح",
                                    tint = DrovaTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DrovaSurfaceVariant,
                        unfocusedContainerColor = DrovaSurfaceVariant,
                        focusedBorderColor = DrovaTurquoise,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_restaurant_orders")
                )

                // Horizontal Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(OrderQueueFilter.values()) { filter ->
                        val count = when (filter) {
                            OrderQueueFilter.ALL -> allRestaurantOrders.size
                            OrderQueueFilter.NEW -> allRestaurantOrders.count { it.status == OrderStatus.CREATED || it.status == OrderStatus.RESTAURANT_CONFIRMED }
                            OrderQueueFilter.PREPARING -> allRestaurantOrders.count { it.status == OrderStatus.PREPARING }
                            OrderQueueFilter.READY -> allRestaurantOrders.count { it.status == OrderStatus.READY_FOR_PICKUP }
                            OrderQueueFilter.COMPLETED -> allRestaurantOrders.count { it.status.isTerminal || it.status == OrderStatus.DELIVERED || it.status == OrderStatus.ON_THE_WAY || it.status == OrderStatus.PICKED_UP || it.status == OrderStatus.CAPTAIN_ASSIGNED }
                        }
                        val isSelected = selectedFilter == filter

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) DrovaTurquoise else DrovaSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) DrovaTurquoise else DrovaBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { restaurantViewModel.selectQueueFilter(filter) }
                                .testTag("filter_queue_${filter.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAr) filter.titleAr else filter.titleEn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else DrovaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Color.White.copy(alpha = 0.25f) else DrovaBorder,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else DrovaTextSecondary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Orders List
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = DrovaTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isAr) "لا توجد طلبات في هذا القسم" else "No orders in this category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) "الطلبات الواردة ستظهر فوراً في طابور التشغيل" else "Incoming orders will appear in real time",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    RestaurantQueueOrderCard(
                        order = order,
                        onConfirm = { restaurantViewModel.confirmOrder(order.id) },
                        onStartPrep = { restaurantViewModel.startPreparing(order.id) },
                        onMarkReady = { restaurantViewModel.markReadyForPickup(order.id) },
                        onReject = { orderToReject = order },
                        onClick = { restaurantViewModel.openOrderDetail(order) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantQueueOrderCard(
    order: Order,
    onConfirm: () -> Unit,
    onStartPrep: () -> Unit,
    onMarkReady: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("order_queue_card_${order.id}"),
        shape = RoundedCornerShape(16.dp),
        color = DrovaSurface,
        border = BorderStroke(
            1.dp,
            if (order.status == OrderStatus.CREATED) DrovaWarning.copy(alpha = 0.8f)
            else if (order.status == OrderStatus.RESTAURANT_CONFIRMED) DrovaTurquoise.copy(alpha = 0.5f)
            else DrovaBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Number, Payment Info, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DrovaSurfaceVariant
                    ) {
                        Text(
                            text = order.paymentMethod.titleAr,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (order.paymentStatus == PaymentStatus.PAID) DrovaSuccessContainer else DrovaWarningContainer
                    ) {
                        Text(
                            text = order.paymentStatus.titleAr,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (order.paymentStatus == PaymentStatus.PAID) DrovaSuccessText else DrovaWarningText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                DrovaStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Customer Name & Creation Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = DrovaTurquoise,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${order.customerName} (${order.customerPhone})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DrovaTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${order.createdAtFormatted}",
                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                )
            }

            // Delivery Address
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = DrovaTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.deliveryAddressAr,
                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Items List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${item.quantity}x",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.nameAr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = DrovaTextPrimary
                                ),
                                maxLines = 1
                            )
                        }

                        Text(
                            text = "${item.totalEgp} ج.م",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DrovaTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    if (item.notes.isNotBlank()) {
                        Text(
                            text = "• ${item.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }
            }

            if (order.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DrovaWarningContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ملاحظة العميل: ${order.specialInstructions}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaWarningText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // If rejected or cancelled, show banner
            if (order.status == OrderStatus.REJECTED && !order.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DrovaErrorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "سبب الرفض: ${order.rejectionReason}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaErrorText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (order.status == OrderStatus.CANCELLED && !order.cancellationReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DrovaErrorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "سبب الإلغاء: ${order.cancellationReason}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaErrorText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Subtotal, Delivery Fee & Total Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المجموع الفرعي: ${order.subtotalEgp} ج.م • التوصيل: ${order.deliveryFeeEgp} ج.م",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isAr) "الإجمالي: " else "Total: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                    )
                    Text(
                        text = "${order.totalEgp} ج.م",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaCharcoal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Operational Action Buttons (STRICT RESTAURANT TRANSITIONS)
            when (order.status) {
                OrderStatus.CREATED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onReject,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DrovaErrorContainer,
                                contentColor = DrovaErrorText
                            )
                        ) {
                            Text(
                                text = if (isAr) "رفض الطلب" else "Reject",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
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
                                text = if (isAr) "تأكيد الطلب" else "Confirm Order",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                OrderStatus.RESTAURANT_CONFIRMED -> {
                    Button(
                        onClick = onStartPrep,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
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
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                OrderStatus.PREPARING -> {
                    DrovaPrimaryButton(
                        text = if (isAr) "جاهز للاستلام والتسليم للكابتن" else "Mark Ready for Captain Pickup",
                        onClick = onMarkReady,
                        leadingIcon = Icons.Default.CheckCircle,
                        testTag = "queue_btn_ready_${order.id}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OrderStatus.READY_FOR_PICKUP, OrderStatus.CAPTAIN_ASSIGNED -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DrovaTurquoiseLight
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = DrovaTurquoiseHover,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "جاهز للاستلام - بانتظار استلام الكابتن" else "Ready - Awaiting Captain Handover",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DrovaSurfaceVariant
                    ) {
                        Text(
                            text = if (isAr) "الطلب في الطريق إلى العميل مع الكابتن" else "On the way with delivery captain",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                else -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = if (order.status.isFailure) DrovaErrorContainer else DrovaSuccessContainer
                    ) {
                        Text(
                            text = if (order.status.isFailure)
                                (if (isAr) "تم إلغاء أو رفض الطلب" else "Cancelled / Rejected")
                            else
                                (if (isAr) "تم تسليم الطلب بنجاح" else "Delivered Successfully"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (order.status.isFailure) DrovaErrorText else DrovaSuccessText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
