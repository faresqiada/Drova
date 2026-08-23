package com.example.presentation.restaurant.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.presentation.restaurant.SettlementRecord
import com.example.ui.theme.*

@Composable
fun RestaurantFinanceTab(
    restaurantViewModel: RestaurantViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val restaurant by restaurantViewModel.restaurantData.collectAsState()
    val todaySalesEgp by restaurantViewModel.todaySalesEgp.collectAsState()
    val todayOrdersCount by restaurantViewModel.todayOrdersCount.collectAsState()
    val grossSalesSumEgp by restaurantViewModel.grossSalesSumEgp.collectAsState()
    val commissionDeductedEgp by restaurantViewModel.commissionDeductedEgp.collectAsState()
    val restaurantOrders by restaurantViewModel.restaurantOrders.collectAsState()
    val netSettlementBalanceEgp by restaurantViewModel.netSettlementBalanceEgp.collectAsState()
    val settlements = restaurantViewModel.settlementHistory

    val weeklySalesEgp = grossSalesSumEgp
    val totalOrdersCount = restaurantOrders.count { it.status != com.example.domain.model.OrderStatus.CANCELLED && it.status != com.example.domain.model.OrderStatus.REJECTED }
    val completedSettlementsSumEgp = settlements.sumOf { it.netPayoutEgp }

    var showPayoutSuccessDialog by remember { mutableStateOf(false) }

    if (showPayoutSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showPayoutSuccessDialog = false },
            title = {
                Text(
                    text = if (isAr) "طلب التحويل الفوري" else "Instant Payout Request",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isAr) "تم استلام طلب التحويل الفوري بنجاح. سيتم إيداع مبلغ التسوية المستحق في حسابكم البنكي المعتمد خلال ساعتين عمل."
                    else "Instant payout request received. Funds will be deposited within 2 business hours."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPayoutSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                ) {
                    Text(if (isAr) "حسناً" else "OK")
                }
            },
            containerColor = DrovaSurface
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_finance_tab"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Card: Pending Net Settlement Balance (Pending Settlement)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = DrovaCharcoal
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "رصيد التسوية المستحقة (Pending Settlement)" else "Pending Payout Settlement",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = DrovaCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${"%,.2f".format(netSettlementBalanceEgp)} ج.م",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaCyanAccent,
                            fontSize = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "موعد التحويل التلقائي القادم:" else "Next automated transfer:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = if (isAr) "الأحد القادم، 10:00 صباحاً" else "Next Sunday, 10:00 AM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = { showPayoutSuccessDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise),
                            modifier = Modifier.testTag("btn_request_instant_payout")
                        ) {
                            Text(
                                text = if (isAr) "طلب تحويل فوري" else "Instant Payout",
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

        // 2. Financial Metrics Overview Grid (Today's sales, Weekly sales, Total orders, Completed settlement)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "نظرة عامة على المبيعات والعمليات" else "Sales & Revenue Overview",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaCharcoal
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FinanceMetricBox(
                        title = if (isAr) "مبيعات اليوم" else "Today's Sales",
                        value = "${"%,.0f".format(todaySalesEgp)} ج.م",
                        subtitle = if (isAr) "$todayOrdersCount طلب اليوم" else "$todayOrdersCount orders today",
                        icon = Icons.Default.Today,
                        color = DrovaTurquoiseHover,
                        modifier = Modifier.weight(1f)
                    )
                    FinanceMetricBox(
                        title = if (isAr) "المبيعات الأسبوعية" else "Weekly Sales",
                        value = "${"%,.0f".format(weeklySalesEgp)} ج.م",
                        subtitle = if (isAr) "آخر 7 أيام" else "Last 7 days",
                        icon = Icons.Default.DateRange,
                        color = DrovaCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FinanceMetricBox(
                        title = if (isAr) "إجمالي الطلبات" else "Total Orders",
                        value = "$totalOrdersCount طلب",
                        subtitle = if (isAr) "طلبات ناجحة" else "Completed orders",
                        icon = Icons.Default.ReceiptLong,
                        color = DrovaCharcoal,
                        modifier = Modifier.weight(1f)
                    )
                    FinanceMetricBox(
                        title = if (isAr) "تسويات مكتملة" else "Completed Settlements",
                        value = "${"%,.0f".format(completedSettlementsSumEgp)} ج.م",
                        subtitle = if (isAr) "تم تحويلها بنجاح" else "Transferred to bank",
                        icon = Icons.Default.CheckCircle,
                        color = DrovaSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Financial Breakdown Card (Gross sales, DROVA commission, Platform fees, Net settlement)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "هيكل الحسابات واستقطاعات المنصة" else "Revenue Breakdown & Deductions",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FinancialRowItem(
                        label = if (isAr) "إجمالي المبيعات (Gross Sales)" else "Gross Sales",
                        value = "${"%,.2f".format(grossSalesSumEgp)} ج.م",
                        valueColor = DrovaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FinancialRowItem(
                        label = if (isAr) "نسبة عمولة منصة DROVA (${restaurant?.commissionRatePercent ?: 12.0}%)" else "DROVA Commission Rate",
                        value = "- ${"%,.2f".format(commissionDeductedEgp)} ج.م",
                        valueColor = DrovaErrorText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FinancialRowItem(
                        label = if (isAr) "رسوم المنصة والخدمة (Platform Fees)" else "Platform & Tech Fees",
                        value = "مشمولة بالكامل",
                        valueColor = DrovaSuccessText
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    FinancialRowItem(
                        label = if (isAr) "صافي مستحقات المطعم (Net Settlement)" else "Net Settlement Amount",
                        value = "${"%,.2f".format(netSettlementBalanceEgp)} ج.م",
                        valueColor = DrovaTurquoiseHover
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    FinancialRowItem(
                        label = if (isAr) "الحساب البنكي المعتمد للتحويل" else "Designated Bank Account",
                        value = if (isAr) "البنك الأهلي المصري (**** 4829)" else "NBE Bank (**** 4829)",
                        valueColor = DrovaTextPrimary
                    )
                }
            }
        }

        // 4. Past Settlements History Section (Completed Settlements)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "سجل التحويلات والتسويات المكتملة (Completed Settlements)" else "Completed Settlements History",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaCharcoal
                    )
                )

                settlements.forEach { settlement ->
                    SettlementHistoryCard(settlement = settlement)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FinanceMetricBox(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
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
                        color = DrovaTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = color,
                    fontSize = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = DrovaTextSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun FinancialRowItem(
    label: String,
    value: String,
    valueColor: Color
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
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        )
    }
}

@Composable
private fun SettlementHistoryCard(
    settlement: SettlementRecord
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = settlement.periodAr,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Text(
                        text = "${settlement.dateFormatted} • مرجع: ${settlement.referenceNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DrovaSuccessContainer
                ) {
                    Text(
                        text = settlement.statusAr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DrovaSuccessText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isAr) "عدد الطلبات" else "Orders",
                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = "${settlement.ordersCount} طلب",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = DrovaTextPrimary)
                    )
                }

                Column {
                    Text(
                        text = if (isAr) "إجمالي المبيعات" else "Gross Sales",
                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = "${"%,.0f".format(settlement.grossSalesEgp)} ج.م",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = DrovaTextPrimary)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isAr) "صافي المحول" else "Net Payout",
                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = "${"%,.0f".format(settlement.netPayoutEgp)} ج.م",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = DrovaTurquoiseHover
                        )
                    )
                }
            }
        }
    }
}
