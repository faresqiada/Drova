package com.example.presentation.captain.notifications

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.AppLanguage
import com.example.core.designsystem.DrovaLanguageManager
import com.example.domain.model.CaptainNotification
import com.example.domain.model.CaptainNotificationType
import com.example.ui.theme.*

@Composable
fun CaptainNotificationDialog(
    notifications: List<CaptainNotification>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "إشعارات وتنبيهات الكابتن" else "Captain Notifications",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaTextPrimary
                    )
                )
                if (notifications.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text(
                            text = if (isAr) "تحديد الكل كمقروء" else "Mark All Read",
                            style = MaterialTheme.typography.labelSmall.copy(color = DrovaTurquoiseHover)
                        )
                    }
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = DrovaTextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr) "لا توجد إشعارات جديدة حالياً" else "No new notifications",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DrovaTextSecondary)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onMarkRead(notif.id) }
                                .testTag("notif_item_${notif.id}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (notif.isRead) DrovaSurface else DrovaTurquoiseLight,
                            border = BorderStroke(0.5.dp, if (notif.isRead) DrovaBorder else DrovaTurquoise)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (notif.type) {
                                                CaptainNotificationType.NEW_REQUEST -> DrovaTurquoise
                                                CaptainNotificationType.REQUEST_ACCEPTED -> DrovaCharcoal
                                                CaptainNotificationType.RESTAURANT_READY -> DrovaTurquoiseHover
                                                CaptainNotificationType.CUSTOMER_DESTINATION -> DrovaCharcoal
                                                CaptainNotificationType.DELIVERY_COMPLETED -> DrovaSuccess
                                                CaptainNotificationType.EARNINGS_CREDITED -> DrovaCyanAccent
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            CaptainNotificationType.NEW_REQUEST -> Icons.Default.Moped
                                            CaptainNotificationType.REQUEST_ACCEPTED -> Icons.Default.Check
                                            CaptainNotificationType.RESTAURANT_READY -> Icons.Default.Storefront
                                            CaptainNotificationType.CUSTOMER_DESTINATION -> Icons.Default.Navigation
                                            CaptainNotificationType.DELIVERY_COMPLETED -> Icons.Default.CheckCircle
                                            CaptainNotificationType.EARNINGS_CREDITED -> Icons.Default.AccountBalanceWallet
                                        },
                                        contentDescription = null,
                                        tint = if (notif.type == CaptainNotificationType.EARNINGS_CREDITED) DrovaCharcoal else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) notif.titleAr else notif.titleEn,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (notif.isRead) FontWeight.Medium else FontWeight.Bold,
                                            color = DrovaTextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isAr) notif.messageAr else notif.messageEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DrovaTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.timestampFormatted,
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
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
            ) {
                Text(if (isAr) "إغلاق" else "Close")
            }
        },
        containerColor = DrovaSurface
    )
}
