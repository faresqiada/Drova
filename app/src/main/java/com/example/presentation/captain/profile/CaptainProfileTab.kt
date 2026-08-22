package com.example.presentation.captain.profile

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
import com.example.domain.model.DeliveryTask
import com.example.domain.model.PaymentMethod
import com.example.domain.model.UserRole
import com.example.presentation.captain.CaptainViewModel
import com.example.ui.theme.*

@Composable
fun CaptainProfileTab(
    captainViewModel: CaptainViewModel,
    onRoleSwitch: (UserRole) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val currentUser by captainViewModel.currentUser.collectAsState()
    val captainMode by captainViewModel.captainMode.collectAsState()
    val earnings by captainViewModel.earnings.collectAsState()
    val completedTasks by captainViewModel.completedTasks.collectAsState()
    val selectedTask by captainViewModel.selectedHistoryTask.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("captain_profile_tab")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Captain Profile Hero Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("captain_profile_hero_card"),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(DrovaTurquoiseLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBike,
                                contentDescription = null,
                                tint = DrovaTurquoise,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser?.fullName ?: "محمود عادل",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "موثق",
                                    tint = DrovaTurquoise,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = currentUser?.phone ?: "+201198765432",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DrovaSuccessContainer
                                ) {
                                    Text(
                                        text = if (isAr) "الحساب نشط وموثق" else "Verified & Active",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = DrovaSuccessText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats row: Rating, Completed Trips, On-Time Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = DrovaWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "4.9",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = if (isAr) "التقييم العام" else "Rating",
                                style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${earnings.todayDeliveriesCount + 334}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoiseHover
                                )
                            )
                            Text(
                                text = if (isAr) "إجمالي الرحلات" else "Total Deliveries",
                                style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${earnings.onTimeDeliveryRatePercent}%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaSuccessText
                                )
                            )
                            Text(
                                text = if (isAr) "التسليم بالموعد" else "On-Time Rate",
                                style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                            )
                        }
                    }
                }
            }
        }

        // 2. Vehicle & Verification Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "بيانات المركبة والتوثيق" else "Vehicle & Verification",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "نوع المركبة:" else "Vehicle Type:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = currentUser?.vehicleType ?: "دراجة نارية (هوندا 150cc)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "رقم اللوحة:" else "Plate Number:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "أ ب ج 4 5 8 9",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "رخصة القيادة والفيش:" else "Documents:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = "سارية وموثقة رسمياً ✓",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaSuccessText
                            )
                        )
                    }
                }
            }
        }

        // 3. Completed Deliveries History Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "سجل الرحلات المكتملة" else "Completed Deliveries History",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaCharcoal
                    )
                )
                Text(
                    text = "${completedTasks.size} ${if (isAr) "رحلات" else "trips"}",
                    style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                )
            }
        }

        // 4. Completed Deliveries List
        items(completedTasks, key = { it.orderId }) { task ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { captainViewModel.selectHistoryTask(task) }
                    .testTag("completed_task_row_${task.orderId}"),
                shape = RoundedCornerShape(12.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
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
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary
                                )
                            )
                        }

                        Text(
                            text = "+${task.estimatedEarningsEgp} ج.م",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaSuccessText
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "التسليم: ${task.customerAddressAr} (${task.customerName})",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.createdAtFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextMuted)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) "كاش (${task.orderTotalEgp} ج.م)" else "إلكتروني",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) DrovaWarningText else DrovaSuccessText
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = DrovaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Role Switcher for Reviewer
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DrovaSurfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "التبديل بين وحدات نظام DROVA" else "Switch Platform Roles:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onRoleSwitch(UserRole.CUSTOMER) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_switch_customer_from_profile"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                        ) {
                            Text(
                                text = if (isAr) "تطبيق العميل" else "Customer",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Button(
                            onClick = { onRoleSwitch(UserRole.RESTAURANT) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_switch_restaurant_from_profile"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaCharcoal)
                        ) {
                            Text(
                                text = if (isAr) "لوحة المطعم" else "Restaurant",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // 6. Logout Button
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("captain_logout_full_btn"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, DrovaError)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = DrovaError,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "تسجيل الخروج من حساب الكابتن" else "Logout from Captain Account",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaError
                    )
                )
            }
        }
    }

    // Completed Trip Details Bottom Sheet / Dialog
    if (selectedTask != null) {
        val task = selectedTask!!
        AlertDialog(
            onDismissRequest = { captainViewModel.selectHistoryTask(null) },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تفاصيل الرحلة ${task.orderNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    DrovaStatusBadge(status = task.status)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "المطعم: ${task.restaurantNameAr}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "الاستلام: ${task.restaurantAddressAr}",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "العميل: ${task.customerName} (${task.customerPhone})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "التسليم: ${task.customerAddressAr}",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "الأصناف: ${task.itemsSummary}",
                        style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "طريقة الدفع:",
                            style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                        )
                        Text(
                            text = if (task.paymentMethod == PaymentMethod.CASH_ON_DELIVERY) "نقداً (${task.orderTotalEgp} ج.م)" else "إلكتروني مدفوع",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "عائد الكابتن الصافي:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "+${task.estimatedEarningsEgp} ج.م",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaSuccessText
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { captainViewModel.selectHistoryTask(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                ) {
                    Text(if (isAr) "إغلاق" else "Close")
                }
            },
            containerColor = DrovaSurface
        )
    }
}
