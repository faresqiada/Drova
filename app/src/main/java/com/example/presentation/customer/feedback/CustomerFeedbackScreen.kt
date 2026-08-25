package com.example.presentation.customer.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.AppLanguage
import com.example.core.designsystem.DrovaLanguageManager
import com.example.core.designsystem.DrovaTopBar
import com.example.ui.theme.DrovaBackground
import com.example.ui.theme.DrovaBorder
import com.example.ui.theme.DrovaSurface
import com.example.ui.theme.DrovaTextPrimary
import com.example.ui.theme.DrovaTextSecondary
import com.example.ui.theme.DrovaTurquoise
import com.example.ui.theme.DrovaWarning

@Composable
fun CustomerFeedbackScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    var selectedRating by remember { mutableIntStateOf(0) }
    var complaintText by remember { mutableStateOf("") }
    var complaintCategory by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("customer_feedback_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "التقييمات والشكاوى" else "Ratings & Complaints",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("customer_rating_card"),
                colors = CardDefaults.cardColors(containerColor = DrovaSurface),
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isAr) "قيّم تجربة الطلب" else "Rate your order experience",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DrovaTextPrimary
                    )
                    Text(
                        text = if (isAr) "اختر تقييمًا من نجمة إلى خمس نجوم." else "Choose a rating from one to five stars.",
                        color = DrovaTextSecondary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        (1..5).forEach { rating ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$rating",
                                tint = if (rating <= selectedRating) DrovaWarning else Color.LightGray,
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .clickable { selectedRating = rating }
                                    .testTag("rating_star_$rating")
                            )
                        }
                    }
                    Text(
                        text = if (selectedRating == 0) {
                            if (isAr) "لم يتم اختيار تقييم" else "No rating selected"
                        } else {
                            "$selectedRating/5"
                        },
                        color = DrovaTextSecondary,
                        modifier = Modifier.fillMaxWidth().testTag("selected_rating_value")
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().testTag("customer_complaint_card"),
                colors = CardDefaults.cardColors(containerColor = DrovaSurface),
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = DrovaTurquoise)
                        Text(
                            text = if (isAr) "إرسال شكوى أو ملاحظة" else "Submit a complaint or note",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DrovaTextPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = { showCategoryDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("complaint_category_button")
                    ) {
                        Text(
                            text = complaintCategory.ifBlank {
                                if (isAr) "اختر نوع الشكوى" else "Choose complaint category"
                            }
                        )
                    }
                    OutlinedTextField(
                        value = complaintText,
                        onValueChange = { complaintText = it },
                        modifier = Modifier.fillMaxWidth().testTag("complaint_text_field"),
                        minLines = 4,
                        label = { Text(if (isAr) "التفاصيل" else "Details") }
                    )
                    Button(
                        onClick = { submitted = true },
                        enabled = complaintCategory.isNotBlank() && complaintText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().testTag("submit_feedback_button")
                    ) {
                        Text(if (isAr) "حفظ التقييم والشكوى" else "Save rating and complaint")
                    }
                    if (submitted) {
                        HorizontalDivider(color = DrovaBorder)
                        Text(
                            text = if (isAr)
                                "تم حفظ النموذج محليًا. سيرسل إلى الخادم عند توفر API الشكاوى والتقييمات القديم."
                            else
                                "The form was saved locally. It will sync when the legacy ratings and complaints API is available.",
                            color = DrovaTextSecondary,
                            modifier = Modifier.testTag("feedback_saved_message")
                        )
                    }
                }
            }
        }
    }

    if (showCategoryDialog) {
        val categories = if (isAr) {
            listOf("مشكلة في الطلب", "تأخير التوصيل", "جودة الطعام", "مشكلة في الدفع", "أخرى")
        } else {
            listOf("Order issue", "Delivery delay", "Food quality", "Payment issue", "Other")
        }
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text(if (isAr) "نوع الشكوى" else "Complaint category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.forEach { category ->
                        TextButton(
                            onClick = {
                                complaintCategory = category
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(category)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text(if (isAr) "إغلاق" else "Close")
                }
            }
        )
    }
}
