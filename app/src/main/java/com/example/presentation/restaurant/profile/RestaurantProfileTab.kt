package com.example.presentation.restaurant.profile

import androidx.compose.foundation.BorderStroke
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
import com.example.domain.model.Restaurant
import com.example.domain.model.UserRole
import com.example.presentation.restaurant.RestaurantViewModel
import com.example.ui.theme.*

@Composable
fun RestaurantProfileTab(
    restaurantViewModel: RestaurantViewModel,
    onRoleSwitch: (UserRole) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val restaurant by restaurantViewModel.restaurantData.collectAsState()
    val currentUser by restaurantViewModel.currentUser.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    if (showEditProfileDialog && restaurant != null) {
        EditStoreProfileDialog(
            restaurant = restaurant!!,
            onSave = { name, desc, addr, phone, hours ->
                restaurantViewModel.updateStoreProfile(name, desc, addr, phone, hours)
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("restaurant_profile_tab"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Restaurant Header Identity Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            DrovaMark(size = 48.dp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = restaurant?.nameAr ?: (currentUser?.businessName ?: "شاورما الريم المعادي"),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = DrovaTextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${restaurant?.categoryAr ?: "شاورما ومشويات"} • ⭐ ${restaurant?.rating ?: 4.8} (${restaurant?.reviewCount ?: 1420} تقييم)",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextSecondary)
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("btn_edit_store_profile")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل البيانات",
                                tint = DrovaTurquoise
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact & Location Rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DrovaTurquoise,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = restaurant?.addressAr ?: "شارع 9، المعادي، القاهرة",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = DrovaTurquoise,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = restaurant?.phone ?: "+20 100 887 9922",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = DrovaTurquoise,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = restaurant?.openingHours ?: "11:00 ص - 02:00 ص (يومياً)",
                                style = MaterialTheme.typography.bodySmall.copy(color = DrovaTextPrimary)
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Subscription & Partner Tier Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DrovaSurface,
                border = BorderStroke(1.5.dp, DrovaTurquoise.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = DrovaTurquoise,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = restaurant?.activeSubscriptionTier ?: "DROVA Pro Partner",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTextPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DrovaSuccessContainer
                        ) {
                            Text(
                                text = if (isAr) "اشتراك نشط" else "Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DrovaSuccessText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "450.00 ج.م / شهرياً • موعد التجديد: ${restaurant?.subscriptionRenewalDate ?: "15 سبتمبر 2026"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DrovaTurquoiseHover,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Tier Included Benefits
                    val benefits = listOf(
                        "أولوية الظهور للعملاء في نطاق المعادي والقاهرة",
                        "نسبة عمولة مخفضة 12% على جميع الطلبات",
                        "توزيع وتوجيه تلقائي ذكي لأقرب كباتن DROVA",
                        "لوحة تحكم تشغيلية متقدمة وإشعارات حية فورية"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        benefits.forEach { benefit ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DrovaSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DrovaTextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Language & Settings
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DrovaSurface,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAr) "إعدادات التطبيق" else "App Settings",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "لغة التطبيق (العربية / English)" else "App Language",
                            style = MaterialTheme.typography.bodyMedium.copy(color = DrovaTextPrimary)
                        )
                        DrovaLanguageToggle()
                    }
                }
            }
        }

        // 4. Role Switcher for Reviewer
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DrovaSurfaceVariant,
                border = BorderStroke(1.dp, DrovaBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAr) "تبديل الواجهة للتجربة:" else "Preview Role Views:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onRoleSwitch(UserRole.CUSTOMER) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "تطبيق العميل" else "Customer App",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Button(
                            onClick = { onRoleSwitch(UserRole.CAPTAIN) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrovaCyanAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBike,
                                contentDescription = null,
                                tint = DrovaCharcoal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "تطبيق الكابتن" else "Captain App",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCharcoal
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Logout Button
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("restaurant_logout_action"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DrovaErrorText),
                border = BorderStroke(1.dp, DrovaErrorText.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = DrovaErrorText,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "تسجيل الخروج من حساب المطعم" else "Logout from Partner Account",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditStoreProfileDialog(
    restaurant: Restaurant,
    onSave: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    var nameAr by remember { mutableStateOf(restaurant.nameAr) }
    var descriptionAr by remember { mutableStateOf(restaurant.descriptionAr) }
    var addressAr by remember { mutableStateOf(restaurant.addressAr) }
    var phone by remember { mutableStateOf(restaurant.phone) }
    var openingHours by remember { mutableStateOf(restaurant.openingHours) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isAr) "تعديل بيانات المطعم" else "Edit Restaurant Profile",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nameAr,
                    onValueChange = { nameAr = it },
                    label = { Text("اسم المطعم") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = addressAr,
                    onValueChange = { addressAr = it },
                    label = { Text("العنوان التفصيلي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = openingHours,
                    onValueChange = { openingHours = it },
                    label = { Text("ساعات العمل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descriptionAr,
                    onValueChange = { descriptionAr = it },
                    label = { Text("نبذة عن المطعم") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(nameAr, descriptionAr, addressAr, phone, openingHours)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DrovaTurquoise)
            ) {
                Text(if (isAr) "حفظ التعديلات" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Cancel")
            }
        },
        containerColor = DrovaSurface
    )
}
