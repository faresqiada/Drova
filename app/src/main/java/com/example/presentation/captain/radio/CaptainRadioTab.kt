package com.example.presentation.captain.radio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.AppLanguage
import com.example.core.designsystem.DrovaLanguageManager
import com.example.domain.model.CaptainNotification
import com.example.presentation.captain.CaptainViewModel
import com.example.ui.theme.DrovaBackground
import com.example.ui.theme.DrovaBorder
import com.example.ui.theme.DrovaSurface
import com.example.ui.theme.DrovaTextPrimary
import com.example.ui.theme.DrovaTextSecondary
import com.example.ui.theme.DrovaTurquoise

@Composable
fun CaptainRadioTab(
    captainViewModel: CaptainViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val notifications by captainViewModel.notifications.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("captain_radio_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DrovaSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Radio, contentDescription = null, tint = DrovaTurquoise)
                    Text(
                        text = if (isAr) "اللاسلكي التشغيلي" else "Operations Radio",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = DrovaTextPrimary
                    )
                    Text(
                        text = if (isAr)
                            "تنبيهات التشغيل للكباتن. يمكنك قراءة الرسائل ومتابعة رحلتك الحالية دون انتقال إجباري."
                        else
                            "Operational updates for captains. Read messages while keeping your current trip uninterrupted.",
                        color = DrovaTextSecondary
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                Text(
                    text = if (isAr) "لا توجد رسائل تشغيلية جديدة." else "No operational messages yet.",
                    color = Color.Gray,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            items(notifications, key = { it.id }) { notification ->
                RadioMessageCard(
                    notification = notification,
                    isAr = isAr,
                    onRead = { captainViewModel.markNotificationAsRead(notification.id) }
                )
            }
        }
    }
}

@Composable
private fun RadioMessageCard(
    notification: CaptainNotification,
    isAr: Boolean,
    onRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("radio_message_${notification.id}"),
        onClick = onRead,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) DrovaSurface else DrovaTurquoise.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, DrovaBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = DrovaTurquoise)
                Text(
                    text = if (isAr) notification.titleAr else notification.titleEn,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DrovaTextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = if (isAr) notification.messageAr else notification.messageEn,
                color = DrovaTextSecondary
            )
            Text(
                text = notification.timestampFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
