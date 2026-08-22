package com.example.presentation.captain.wallet

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
import com.example.domain.model.CaptainTransaction
import com.example.domain.model.CaptainTransactionType
import com.example.presentation.captain.CaptainViewModel
import com.example.ui.theme.*

@Composable
fun CaptainWalletTab(
    captainViewModel: CaptainViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val earnings by captainViewModel.earnings.collectAsState()
    val transactions by captainViewModel.transactions.collectAsState()
    var showPayoutSheet by remember { mutableStateOf(false) }
    var payoutAmountInput by remember { mutableStateOf("500") }
    var selectedPayoutMethod by remember { mutableStateOf("فودافون كاش (01012345678)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("captain_wallet_tab")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Main Wallet Balance Card (Charcoal with Cyan Accent)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("captain_wallet_balance_card"),
                shape = RoundedCornerShape(16.dp),
                color = DrovaCharcoal
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "الرصيد المتاح للسحب الفوري" else "Available Wallet Balance",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${earnings.walletBalanceEgp} ج.م",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaCyanAccent,
                                    fontSize = 28.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = DrovaCyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "أرباح الأسبوع" else "Week Earnings",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = "${earnings.weekEarningsEgp} ج.م",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isAr) "أرباح اليوم" else "Today's Net",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
                            )
                            Text(
                                text = "${earnings.todayNetEarningsEgp} ج.م",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCyanAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showPayoutSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_request_payout_open"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SendToMobile,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "طلب تحويل أرباح فوري" else "Request Instant Payout",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 2. Earnings Breakdown Breakdown Grid
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "تفاصيل الحساب والأرباح" else "Earnings Breakdown",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "الأجر الأساسي للرحلات/الورديات:" else "Base Earnings:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "${earnings.baseEarningsEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "حوافز ومكافآت الإنجاز:" else "Performance Bonuses:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "+${earnings.bonusesEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaSuccessText
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "استقطاعات وخصومات DROVA:" else "DROVA Deductions:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "${earnings.deductionsEgp} ج.م",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "إجمالي صافي الأرباح اليوم:" else "Total Net Today:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${earnings.todayNetEarningsEgp} ج.م",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaTurquoiseHover
                            )
                        )
                    }
                }
            }
        }

        // 3. Transactions Log Header
        item {
            Text(
                text = if (isAr) "سجل العمليات المالية والمحفظة" else "Transaction History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DrovaCharcoal
                )
            )
        }

        // 4. Transactions List
        items(transactions, key = { it.id }) { tx ->
            TransactionItemRow(tx = tx)
        }
    }

    // Instant Payout Sheet / Dialog
    if (showPayoutSheet) {
        AlertDialog(
            onDismissRequest = { showPayoutSheet = false },
            title = {
                Text(
                    text = if (isAr) "طلب تحويل أرباح فوري" else "Instant Payout Request",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isAr) "الرصيد المتاح حالياً: ${earnings.walletBalanceEgp} ج.م"
                        else "Current Available: ${earnings.walletBalanceEgp} EGP",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTurquoiseHover,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    OutlinedTextField(
                        value = payoutAmountInput,
                        onValueChange = { payoutAmountInput = it },
                        label = { Text(if (isAr) "المبلغ المراد تحويله (ج.م)" else "Amount (EGP)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_payout_amount"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(
                        text = if (isAr) "وسيلة التحويل المعتمدة:" else "Payout Destination:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DrovaSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedPayoutMethod,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextPrimary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payoutAmountInput.toDoubleOrNull() ?: 0.0
                        captainViewModel.requestPayout(amount) { success ->
                            if (success) {
                                showPayoutSheet = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_confirm_payout")
                ) {
                    Text(if (isAr) "تأكيد التحويل الآن" else "Confirm Transfer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutSheet = false }) {
                    Text(if (isAr) "إلغاء" else "Cancel", color = DrovaTextSecondary)
                }
            },
            containerColor = DrovaSurface
        )
    }
}

@Composable
private fun TransactionItemRow(tx: CaptainTransaction) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .testTag("transaction_row_${tx.id}"),
        shape = RoundedCornerShape(12.dp),
        color = DrovaSurface,
        border = BorderStroke(1.dp, DrovaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (tx.isCredit) DrovaSuccessContainer else DrovaSurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            CaptainTransactionType.TRIP_EARNING -> Icons.Default.Moped
                            CaptainTransactionType.SHIFT_BASE -> Icons.Default.AccessTime
                            CaptainTransactionType.BONUS -> Icons.Default.Star
                            CaptainTransactionType.PAYOUT_WITHDRAWAL -> Icons.Default.ArrowOutward
                            CaptainTransactionType.DEDUCTION -> Icons.Default.Remove
                        },
                        contentDescription = null,
                        tint = if (tx.isCredit) DrovaSuccessText else DrovaTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isAr) tx.titleAr else tx.titleEn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    if (tx.referenceOrderNumber != null) {
                        Text(
                            text = "طلب: ${tx.referenceOrderNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(color = DrovaTurquoiseHover)
                        )
                    }
                    Text(
                        text = tx.dateFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (tx.isCredit) "+" else "-"}${tx.amountEgp} ج.م",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (tx.isCredit) DrovaSuccessText else DrovaErrorText
                    )
                )
                Text(
                    text = if (isAr) tx.statusAr else tx.statusEn,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = DrovaTextSecondary
                    )
                )
            }
        }
    }
}
