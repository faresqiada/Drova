package com.example.presentation.restaurant.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.designsystem.AppLanguage
import com.example.core.designsystem.DrovaLanguageManager
import com.example.domain.model.Order
import com.example.ui.theme.*

@Composable
fun RejectOrderDialog(
    order: Order,
    onConfirmReject: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    val standardReasons = if (isAr) {
        listOf(
            "المطعم مزدحم جداً وضغط المطبخ لا يستوعب طلبات جديدة",
            "نفاد بعض المكونات الأساسية لإعداد الوجبة",
            "المطبخ خارج ساعات العمل التشغيلية",
            "تعذر الالتزام بوقت التحضير والتوصيل المطلوب",
            "سبب آخر (توضيح خاص)"
        )
    } else {
        listOf(
            "Kitchen is at maximum capacity",
            "Key ingredients are out of stock",
            "Outside operational kitchen hours",
            "Unable to meet preparation & delivery time",
            "Other operational reason"
        )
    }

    var selectedReason by remember { mutableStateOf(standardReasons.first()) }
    var customReasonText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("reject_order_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = DrovaSurface,
            border = BorderStroke(1.dp, DrovaError.copy(alpha = 0.4f)),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = DrovaErrorText,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "تأكيد رفض الطلب ${order.orderNumber}" else "Reject Order ${order.orderNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إلغاء",
                            tint = DrovaTextSecondary
                        )
                    }
                }

                // Alert Warning Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DrovaErrorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr)
                            "يرجى تحديد سبب الرفض بوضوح. سيتم إشعار العميل فوراً بالاعتذار وتسجيل السبب في سجل عمليات الطلب."
                        else
                            "Please select a rejection reason. The customer will be notified and this will be logged in the order timeline.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaErrorText,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Reasons selection
                Text(
                    text = if (isAr) "سبب الرفض (إلزامي) *" else "Rejection Reason (Required) *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaTextPrimary
                    )
                )

                standardReasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) DrovaTurquoiseLight else DrovaSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) DrovaTurquoise else DrovaBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedReason = reason
                                errorMessage = null
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedReason = reason
                                    errorMessage = null
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = DrovaTurquoise,
                                    unselectedColor = DrovaTextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) DrovaTurquoiseHover else DrovaTextPrimary
                                )
                            )
                        }
                    }
                }

                // Additional details or custom reason
                Column {
                    Text(
                        text = if (isAr) "ملاحظات إضافية / توضيح السبب:" else "Additional notes (Optional/Required if Other):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DrovaTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customReasonText,
                        onValueChange = {
                            customReasonText = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                text = if (isAr) "اكتب توضيحاً إضافياً هنا..." else "Enter specific details...",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                            )
                        },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DrovaSurfaceVariant,
                            unfocusedContainerColor = DrovaSurfaceVariant,
                            focusedBorderColor = DrovaTurquoise,
                            unfocusedBorderColor = DrovaBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_rejection_reason")
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaErrorText,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DrovaTextPrimary),
                        border = BorderStroke(1.dp, DrovaBorder)
                    ) {
                        Text(
                            text = if (isAr) "تراجع" else "Cancel",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val isOther = selectedReason.contains("سبب آخر") || selectedReason.contains("Other")
                            val finalReason = if (isOther && customReasonText.isNotBlank()) {
                                customReasonText.trim()
                            } else if (isOther && customReasonText.isBlank()) {
                                errorMessage = if (isAr) "يرجى كتابة سبب الرفض في المربع أعلاه" else "Please enter rejection details"
                                return@Button
                            } else if (customReasonText.isNotBlank()) {
                                "$selectedReason - ${customReasonText.trim()}"
                            } else {
                                selectedReason
                            }

                            onConfirmReject(finalReason)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .testTag("btn_confirm_reject_order"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DrovaError,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isAr) "تأكيد رفض الطلب" else "Confirm Rejection",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
