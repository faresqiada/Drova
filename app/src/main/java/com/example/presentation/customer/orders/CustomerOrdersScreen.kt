package com.example.presentation.customer.orders

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
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun CustomerOrdersScreen(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val activeOrders by viewModel.activeCustomerOrders.collectAsState()
    val pastOrders by viewModel.pastCustomerOrders.collectAsState()

    var selectedOrderTab by remember { mutableStateOf(0) } // 0 = Active, 1 = History

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DrovaBackground)
            .testTag("customer_orders_screen")
    ) {
        // Tab Selector Row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DrovaSurface,
            border = BorderStroke(0.5.dp, DrovaBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabButton(
                    text = "${if (isAr) "الطلبات الجارية" else "Active Orders"} (${activeOrders.size})",
                    isSelected = selectedOrderTab == 0,
                    onClick = { selectedOrderTab = 0 },
                    modifier = Modifier.weight(1f),
                    testTag = "orders_tab_active"
                )

                FilterTabButton(
                    text = "${if (isAr) "سجل الطلبات السابقة" else "Order History"} (${pastOrders.size})",
                    isSelected = selectedOrderTab == 1,
                    onClick = { selectedOrderTab = 1 },
                    modifier = Modifier.weight(1f),
                    testTag = "orders_tab_history"
                )
            }
        }

        // Orders List
        if (selectedOrderTab == 0) {
            // Active Orders
            if (activeOrders.isEmpty()) {
                EmptyOrdersState(
                    title = if (isAr) "لا توجد طلبات جارية حالياً" else "No Active Orders",
                    subtitle = if (isAr) "عندما تطلب وجبات من المطاعم ستتمكن من تتبعها خطوة بخطوة هنا" else "Live orders will appear here for tracking",
                    buttonText = if (isAr) "اطلب الآن" else "Order Now",
                    onButtonClick = { viewModel.selectTab(com.example.presentation.customer.CustomerTab.HOME) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeOrders, key = { it.id }) { order ->
                        ActiveOrderCard(
                            order = order,
                            onTrackClick = { viewModel.navigateToOrderTracking(order.id) },
                            onAdvanceStep = { viewModel.advanceOrderSimulation(order.id) }
                        )
                    }
                }
            }
        } else {
            // Past Orders History
            if (pastOrders.isEmpty()) {
                EmptyOrdersState(
                    title = if (isAr) "لا يوجد سجل طلبات سابقة" else "No Order History",
                    subtitle = if (isAr) "جميع فواتيرك وطلباتك السابقة ستُحفظ هنا لمراجعتها وإعادة طلبها بسهولة" else "Past completed orders will appear here",
                    buttonText = if (isAr) "تصفح المطاعم" else "Browse Restaurants",
                    onButtonClick = { viewModel.selectTab(com.example.presentation.customer.CustomerTab.HOME) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pastOrders, key = { it.id }) { order ->
                        PastOrderCard(
                            order = order,
                            onDetailsClick = { viewModel.navigateToOrderDetail(order.id) },
                            onReorderClick = { viewModel.reorder(order) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) DrovaPrimary else DrovaSurfaceVariant,
        border = BorderStroke(1.dp, if (isSelected) DrovaPrimary else DrovaBorder),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else DrovaTextPrimary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun ActiveOrderCard(
    order: Order,
    onTrackClick: () -> Unit,
    onAdvanceStep: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_order_card_${order.id}"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.restaurantNameAr,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaTextPrimary,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "${order.orderNumber} • ${order.createdAtFormatted}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                DrovaStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Items Summary
            Text(
                text = order.items.joinToString(" + ") { "${it.quantity} ${it.nameAr}" },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DrovaTextSecondary,
                    fontSize = 12.sp
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isAr) "الإجمالي:" else "Total:"} ${order.totalEgp} ج.م",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaPrimary,
                        fontSize = 13.sp
                    )
                )

                if (order.estimatedArrivalMin > 0) {
                    Text(
                        text = "${if (isAr) "الوصول خلال:" else "ETA:"} ${order.estimatedArrivalMin} ${if (isAr) "دقيقة" else "min"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTurquoiseHover,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DrovaPrimaryButton(
                    text = if (isAr) "تتبع الطلب المباشر 📍" else "Live Track 📍",
                    onClick = onTrackClick,
                    modifier = Modifier.weight(1f),
                    testTag = "track_order_btn_${order.id}"
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DrovaTurquoiseLight,
                    border = BorderStroke(1.dp, DrovaTurquoise.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable(onClick = onAdvanceStep)
                        .testTag("quick_advance_btn_${order.id}")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PastOrderCard(
    order: Order,
    onDetailsClick: () -> Unit,
    onReorderClick: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    DrovaSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("past_order_card_${order.id}"),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.restaurantNameAr,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "${order.orderNumber} • ${order.createdAtFormatted}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                DrovaStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.items.joinToString(" + ") { "${it.quantity} ${it.nameAr}" },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DrovaTextSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.totalEgp} ج.م (${order.paymentMethod.titleAr})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaPrimary,
                        fontSize = 12.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onDetailsClick,
                        modifier = Modifier.testTag("view_details_btn_${order.id}")
                    ) {
                        Text(
                            text = if (isAr) "التفاصيل" else "Receipt",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    DrovaOutlinedButton(
                        text = if (isAr) "إعادة الطلب" else "Reorder",
                        onClick = onReorderClick,
                        modifier = Modifier.width(100.dp),
                        testTag = "reorder_btn_${order.id}"
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersState(
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(DrovaSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = DrovaPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = DrovaDeep
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DrovaTextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        DrovaPrimaryButton(
            text = buttonText,
            onClick = onButtonClick,
            modifier = Modifier.width(200.dp),
            testTag = "empty_orders_action_btn"
        )
    }
}
