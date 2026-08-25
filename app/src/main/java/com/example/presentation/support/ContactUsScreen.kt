package com.example.presentation.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@Composable
fun ContactUsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val context = LocalContext.current
    val availableChannels = listOf(
        Triple(DrovaSupportConfig.phone, if (isAr) "اتصال هاتفي" else "Phone", Icons.Default.Phone),
        Triple(DrovaSupportConfig.email, if (isAr) "البريد الإلكتروني" else "Email", Icons.Default.Email),
        Triple(DrovaSupportConfig.whatsappUrl, if (isAr) "واتساب" else "WhatsApp", Icons.Default.Public),
        Triple(DrovaSupportConfig.facebookUrl, "Facebook", Icons.Default.Public),
        Triple(DrovaSupportConfig.instagramUrl, "Instagram", Icons.Default.Public)
    ).filter { it.first.isNotBlank() }

    fun openChannel(value: String, label: String) {
        val intent = when {
            label == "Phone" || label == "اتصال هاتفي" -> Intent(Intent.ACTION_DIAL, Uri.parse("tel:$value"))
            label == "Email" || label == "البريد الإلكتروني" -> Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$value"))
            else -> Intent(Intent.ACTION_VIEW, Uri.parse(value))
        }
        context.startActivity(Intent.createChooser(intent, label))
    }

    androidx.compose.material3.Scaffold(
        modifier = modifier.fillMaxSize().testTag("contact_us_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "تواصل معنا" else "Contact Us",
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
                border = androidx.compose.foundation.BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = DrovaSupportConfig.organizationName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = DrovaTurquoise
                    )
                    if (DrovaSupportConfig.managerName.isNotBlank()) {
                        Text(
                            text = DrovaSupportConfig.managerName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DrovaTextPrimary
                        )
                    }
                    Spacer(Modifier.padding(4.dp))
                    Text(
                        text = if (isAr) DrovaSupportConfig.descriptionAr else DrovaSupportConfig.descriptionEn,
                        color = DrovaTextSecondary
                    )
                }
            }

            if (availableChannels.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DrovaSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DrovaBorder)
                ) {
                    Text(
                        text = if (isAr)
                            "بيانات التواصل المعتمدة غير مهيأة حاليًا. سيتم تفعيل الأزرار بعد إضافة البيانات من إعدادات التطبيق."
                        else
                            "Approved contact details are not configured yet. Contact buttons will appear after configuration.",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                availableChannels.forEach { (value, label, icon) ->
                    DrovaOutlinedButton(
                        text = label,
                        onClick = { openChannel(value, label) },
                        leadingIcon = icon,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "contact_channel_${label.lowercase().replace(" ", "_")}"
                    )
                }
            }
        }
    }
}
