package com.example.presentation.customer.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.Order
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun OrderDetailScreen(
    order: Order,
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_detail_screen"),
        topBar = {
            DrovaTopBar(
                title = "${if (isAr) "تفاصيل الفاتورة" else "Order Receipt"} #${order.orderNumber}",
                onBackClick = { viewModel.navigateBack() }
            )
        },
        bottomBar = {
            Surface(
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!order.status.isTerminal) {
                        DrovaPrimaryButton(
                            text = if (isAr) "تتبع الطلب المباشر" else "Live Track",
                            onClick = { viewModel.navigateToOrderTracking(order.id) },
                            modifier = Modifier.weight(1f),
                            testTag = "detail_go_to_tracking_btn"
                        )
                    }

                    DrovaOutlinedButton(
                        text = if (isAr) "إعادة طلب الوجبات" else "Reorder Items",
                        onClick = { viewModel.reorder(order) },
                        modifier = Modifier.weight(1f),
                        testTag = "detail_reorder_btn"
                    )
                }
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status & Time Header
            item {
                DrovaSurface(
                    modifier = Modifier.fillMaxWidth(),
                    style = DrovaSurfaceStyle.FLAT,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = order.orderNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaDeep
                                )
                            )
                            Text(
                                text = order.createdAtFormatted,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        DrovaStatusBadge(status = order.status)
                    }
                }
            }

            // Restaurant & Address Info
            item {
                DrovaSectionContainer(
                    title = if (isAr) "بيانات المتجر والتوصيل" else "Store & Delivery Info",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = DrovaPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = order.restaurantNameAr,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DrovaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = order.deliveryAddressAr,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Items List
            item {
                DrovaSectionContainer(
                    title = if (isAr) "قائمة الوجبات (${order.items.size})" else "Meal Items (${order.items.size})",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.quantity} × ${item.nameAr}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    if (item.notes.isNotBlank()) {
                                        Text(
                                            text = "ملاحظة: ${item.notes}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DrovaTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = "${item.unitPriceEgp * item.quantity} ج.م",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Financial Summary
            item {
                DrovaSectionContainer(
                    title = if (isAr) "الفاتورة وطريقة الدفع" else "Payment & Bill Breakdown",
                    style = DrovaSurfaceStyle.FLAT
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "طريقة السداد" else "Payment Method",
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

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "مجموع الأصناف" else "Subtotal",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "${order.subtotalEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "رسوم التوصيل" else "Delivery Fee",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "${order.deliveryFeeEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "رسوم الخدمة" else "Platform Fee",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "${order.platformFeeEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "المبلغ الكلي المدفوع" else "Total Paid",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaDeep
                            )
                        )
                        Text(
                            text = "${order.totalEgp} ج.م",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaPrimary,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
