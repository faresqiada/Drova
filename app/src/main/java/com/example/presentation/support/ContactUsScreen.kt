package com.example.presentation.support

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.config.DrovaSupportConfig
import com.example.core.designsystem.AppLanguage
import com.example.core.designsystem.DrovaLanguageManager
import com.example.core.designsystem.DrovaOutlinedButton
import com.example.core.designsystem.DrovaTopBar
import com.example.ui.theme.DrovaBackground
import com.example.ui.theme.DrovaBorder
import com.example.ui.theme.DrovaSurface
import com.example.ui.theme.DrovaTextPrimary
import com.example.ui.theme.DrovaTextSecondary
import com.example.ui.theme.DrovaTurquoise

data class ContactChannel(
    val id: String,
    val labelAr: String,
    val labelEn: String,
    val value: String,
    val uri: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val action: String
)

private fun approvedChannels(): List<ContactChannel> = listOf(
    ContactChannel(
        id = "whatsapp",
        labelAr = "واتساب",
        labelEn = "WhatsApp",
        value = DrovaSupportConfig.phone,
        uri = DrovaSupportConfig.whatsappUrl,
        icon = Icons.Default.Chat,
        action = "view"
    ),
    ContactChannel(
        id = "phone",
        labelAr = "اتصال هاتفي",
        labelEn = "Phone",
        value = DrovaSupportConfig.phone,
        uri = "tel:${DrovaSupportConfig.phone}",
        icon = Icons.Default.Phone,
        action = "dial"
    ),
    ContactChannel(
        id = "facebook",
        labelAr = "Facebook",
        labelEn = "Facebook",
        value = DrovaSupportConfig.facebookUrl,
        uri = DrovaSupportConfig.facebookUrl,
        icon = Icons.Default.Language,
        action = "view"
    ),
    ContactChannel(
        id = "instagram",
        labelAr = "Instagram",
        labelEn = "Instagram",
        value = DrovaSupportConfig.instagramUrl,
        uri = DrovaSupportConfig.instagramUrl,
        icon = Icons.Default.Language,
        action = "view"
    ),
    ContactChannel(
        id = "tiktok",
        labelAr = "TikTok: ${DrovaSupportConfig.tiktokUsername}",
        labelEn = "TikTok: ${DrovaSupportConfig.tiktokUsername}",
        value = DrovaSupportConfig.tiktokUsername,
        uri = DrovaSupportConfig.tiktokUrl,
        icon = Icons.Default.Language,
        action = "view"
    )
)

private fun openApprovedChannel(context: Context, channel: ContactChannel) {
    val intent = Intent(
        if (channel.action == "dial") Intent.ACTION_DIAL else Intent.ACTION_VIEW,
        Uri.parse(channel.uri)
    )
    try {
        context.startActivity(Intent.createChooser(intent, channel.labelEn))
    } catch (_: ActivityNotFoundException) {
        // The destination is still correct; avoid crashing if a device has no handler.
    }
}

@Composable
fun ContactUsScreen(
    onBackClick: () -> Unit,
    titleAr: String = "تواصل معنا",
    titleEn: String = "Contact Us",
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val context = LocalContext.current
    val channels = approvedChannels()

    androidx.compose.material3.Scaffold(
        modifier = modifier.fillMaxSize().testTag("contact_us_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) titleAr else titleEn,
                onBackClick = onBackClick
            )
        },
        containerColor = DrovaBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DrovaSurface),
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.contact_owner_6315),
                        contentDescription = if (isAr) "صورة مؤسس DROVA" else "DROVA founder photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(480.dp)
                            .testTag("contact_owner_image"),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = DrovaSupportConfig.organizationName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = DrovaTurquoise
                    )
                    Text(
                        text = DrovaSupportConfig.managerName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DrovaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) DrovaSupportConfig.descriptionAr else DrovaSupportConfig.descriptionEn,
                        color = DrovaTextSecondary
                    )
                }
            }

            channels.forEach { channel ->
                DrovaOutlinedButton(
                    text = if (isAr) "${channel.labelAr}: ${channel.value}" else "${channel.labelEn}: ${channel.value}",
                    onClick = { openApprovedChannel(context, channel) },
                    leadingIcon = channel.icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_channel_${channel.id}")
                )
            }
        }
    }
}
