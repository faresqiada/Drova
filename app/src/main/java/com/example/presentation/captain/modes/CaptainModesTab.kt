package com.example.presentation.captain.modes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.presentation.captain.CaptainViewModel
import com.example.ui.theme.*

@Composable
fun CaptainModesTab(
    captainViewModel: CaptainViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val currentMode by captainViewModel.captainMode.collectAsState()
    val shiftData by captainViewModel.shiftData.collectAsState()
    val earnings by captainViewModel.earnings.collectAsState()
    val activeTask by captainViewModel.activeTask.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("captain_modes_tab")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Delivery Block Warning (if active delivery in progress)
        if (activeTask != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DrovaWarningContainer,
                    border = BorderStroke(1.dp, DrovaWarning)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = DrovaWarningText,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAr) "لا يمكن تبديل نظام العمل حالياً لوجود رحلة جارية (${activeTask!!.orderNumber})"
                            else "Cannot switch mode while on an active delivery",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaWarningText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Header Explaining Modes
        item {
            Column {
                Text(
                    text = if (isAr) "أنظمة العمل المتاحة في DROVA" else "DROVA Work Modes",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = DrovaCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAr) "اختر النظام الأنسب لجدولك اليومي. يمكنك التبديل بين الأنظمة في أي وقت عند عدم وجود رحلة جارية."
                    else "Choose the system that fits your schedule. Switch freely when not on an active trip.",
                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                )
            }
        }

        // 1. SHIFT MODE CARD
        item {
            val isSelected = currentMode == CaptainMode.SHIFT_MODE

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = activeTask == null) {
                        captainViewModel.setCaptainMode(CaptainMode.SHIFT_MODE)
                    }
                    .testTag("mode_card_shift"),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) DrovaTurquoise else DrovaBorder
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) DrovaTurquoise else DrovaSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else DrovaTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "نظام الوردية المجدولة" else "Shift Mode",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Text(
                                    text = if (isAr) "أجر أساسي مضمون + حوافز إنجاز" else "Guaranteed hourly rate + delivery bonuses",
                                    style = MaterialTheme.typography.labelSmall.copy(color = DrovaTurquoiseHover)
                                )
                            }
                        }

                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DrovaTurquoiseLight
                            ) {
                                Text(
                                    text = if (isAr) "النظام النشط" else "Active Mode",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTurquoiseHover
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Shift Mode Metrics Grid
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DrovaSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isAr) "الأجر بالساعة المضمون" else "Hourly Guarantee",
                                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                                    )
                                    Text(
                                        text = "${shiftData.hourlyGuaranteedRateEgp} ج.م / ساعة",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isAr) "ساعات العمل اليوم" else "Hours Worked",
                                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                                    )
                                    Text(
                                        text = "${shiftData.hoursWorked} / ${shiftData.scheduledHours} س",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTurquoiseHover
                                        )
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
                                Text(
                                    text = if (isAr) "الدخل الأساسي المكتسب:" else "Base Earnings:",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                )
                                Text(
                                    text = "${shiftData.shiftBaseEarningsEgp} ج.م",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "حوافز التوصيل الإضافية:" else "Delivery Bonuses:",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                                )
                                Text(
                                    text = "+${shiftData.shiftDeliveriesBonusEgp} ج.م",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaSuccessText
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "إجمالي دخل الوردية:" else "Total Shift Earnings:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${shiftData.totalShiftEarningsEgp} ج.م",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = DrovaTurquoiseHover
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isSelected) {
                        DrovaPrimaryButton(
                            text = if (isAr) "التحويل إلى نظام الوردية" else "Switch to Shift Mode",
                            onClick = { captainViewModel.setCaptainMode(CaptainMode.SHIFT_MODE) },
                            enabled = activeTask == null,
                            testTag = "btn_switch_to_shift"
                        )
                    }
                }
            }
        }

        // 2. FREE MODE CARD
        item {
            val isSelected = currentMode == CaptainMode.FREE_MODE

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = activeTask == null) {
                        captainViewModel.setCaptainMode(CaptainMode.FREE_MODE)
                    }
                    .testTag("mode_card_free"),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) DrovaTurquoise else DrovaBorder
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) DrovaTurquoise else DrovaSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Moped,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else DrovaTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "نظام العمل الحر (On-Demand)" else "Free Mode",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTextPrimary
                                    )
                                )
                                Text(
                                    text = if (isAr) "أجر مرن لكل رحلة + مكافآت الذروة" else "Flexible per-trip pay + surge bonuses",
                                    style = MaterialTheme.typography.labelSmall.copy(color = DrovaTurquoiseHover)
                                )
                            }
                        }

                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DrovaTurquoiseLight
                            ) {
                                Text(
                                    text = if (isAr) "النظام النشط" else "Active Mode",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DrovaTurquoiseHover
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Free Mode Metrics Grid
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DrovaSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isAr) "متوسط عائد الرحلة" else "Avg Trip Earning",
                                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                                    )
                                    Text(
                                        text = "48.0 ج.م / رحلة",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTextPrimary
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isAr) "الرحلات المكتملة" else "Trips Done",
                                        style = MaterialTheme.typography.labelSmall.copy(color = DrovaTextSecondary)
                                    )
                                    Text(
                                        text = "${earnings.todayDeliveriesCount} رحلات",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DrovaTurquoiseHover
                                        )
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
                                Text(
                                    text = if (isAr) "أرباح الرحلات المنفذة:" else "Trip Earnings:",
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

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "مكافآت وحوافز الإنجاز:" else "Surge & Bonuses:",
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

                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "إجمالي الأرباح:" else "Total Earnings:",
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isSelected) {
                        DrovaPrimaryButton(
                            text = if (isAr) "التحويل إلى نظام العمل الحر" else "Switch to Free Mode",
                            onClick = { captainViewModel.setCaptainMode(CaptainMode.FREE_MODE) },
                            enabled = activeTask == null,
                            testTag = "btn_switch_to_free"
                        )
                    }
                }
            }
        }
    }
}
