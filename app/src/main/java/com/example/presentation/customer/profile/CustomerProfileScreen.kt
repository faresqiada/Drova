package com.example.presentation.customer.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.domain.model.SavedAddress
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.presentation.customer.CustomerViewModel
import com.example.ui.theme.*

@Composable
fun CustomerProfileScreen(
    user: User?,
    viewModel: CustomerViewModel,
    onSwitchRole: (UserRole) -> Unit,
    onContactUs: () -> Unit,
    onFeedback: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val walletBalance by viewModel.customerWalletBalanceEgp.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()

    var showAddAddressDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DrovaBackground)
            .testTag("customer_profile_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Info Card
        item {
            DrovaSurface(
                modifier = Modifier.fillMaxWidth(),
                style = DrovaSurfaceStyle.FLAT,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(DrovaTurquoiseLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.fullName?.take(1) ?: "ع"),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.fullName ?: "أحمد مصطفى",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = DrovaDeep,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = user?.phone ?: "+20 101 234 5678",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = user?.email ?: "ahmed.mostafa@example.com",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaTextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DrovaTurquoiseLight
                    ) {
                        Text(
                            text = if (isAr) "عميل DROVA" else "Customer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTurquoiseHover,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // DROVA Wallet Card
        item {
            DrovaSurface(
                modifier = Modifier.fillMaxWidth(),
                style = DrovaSurfaceStyle.HERO_DEEP,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = DrovaCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "محفظة DROVA الرقمية" else "DROVA Wallet",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isAr) "نشطة وآمنة" else "Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaCyanAccent,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${walletBalance} ج.م",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr) "الرصيد المتاح للاستخدام في طلبات المطاعم بدون كاش" else "Available balance for fast ordering",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Saved Delivery Addresses Section
        item {
            DrovaSectionContainer(
                title = if (isAr) "عناوين التوصيل المسجلة" else "Saved Delivery Addresses",
                trailingAction = {
                    TextButton(
                        onClick = { showAddAddressDialog = true },
                        modifier = Modifier.testTag("profile_add_address_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = DrovaPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isAr) "إضافة عنوان" else "Add New",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaPrimary
                                )
                            )
                        }
                    }
                },
                style = DrovaSurfaceStyle.FLAT
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    savedAddresses.forEach { address ->
                        AddressItemRow(address = address)
                    }
                }
            }
        }

        // Settings & Language Section
        item {
            DrovaSectionContainer(
                title = if (isAr) "إعدادات التطبيق واللغة" else "App Settings & Language",
                style = DrovaSurfaceStyle.FLAT
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Language Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = DrovaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "لغة التطبيق" else "App Language",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DrovaTextPrimary
                                )
                            )
                        }

                        DrovaLanguageToggle()
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Ratings & Complaints Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onFeedback)
                            .padding(vertical = 8.dp)
                            .testTag("profile_feedback_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.StarRate,
                            contentDescription = null,
                            tint = DrovaWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "التقييمات والشكاوى" else "Ratings & Complaints",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Contact Us Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onContactUs)
                            .padding(vertical = 8.dp)
                            .testTag("profile_contact_us_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContactSupport,
                            contentDescription = null,
                            tint = DrovaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "تواصل معنا" else "Contact Us",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DrovaTextPrimary
                            )
                        )
                    }

                    HorizontalDivider(color = DrovaBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Notifications Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = DrovaTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "إشعارات تتبع الطلبات" else "Order Notifications",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DrovaTextPrimary
                                )
                            )
                        }

                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DrovaPrimary
                            )
                        )
                    }
                }
            }
        }

        // Logout Button
        item {
            DrovaOutlinedButton(
                text = if (isAr) "تسجيل الخروج من الحساب" else "Log Out",
                onClick = onLogout,
                leadingIcon = Icons.Default.Logout,
                modifier = Modifier.fillMaxWidth(),
                testTag = "profile_logout_btn"
            )
        }
    }

    // Add New Address Dialog
    if (showAddAddressDialog) {
        var label by remember { mutableStateOf("") }
        var district by remember { mutableStateOf("") }
        var detailed by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = {
                Text(
                    text = if (isAr) "إضافة عنوان جديد" else "Add New Address",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        placeholder = { Text(if (isAr) "اسم العنوان (مثال: الشقة، المكتب)" else "Address Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        placeholder = { Text(if (isAr) "المنطقة (مثال: المعادي، القاهرة)" else "District / City") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = detailed,
                        onValueChange = { detailed = it },
                        placeholder = { Text(if (isAr) "تفاصيل العنوان (الشارع، العمارة، الشقة)" else "Street, Building, Apt") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (label.isNotBlank() && detailed.isNotBlank()) {
                            viewModel.addNewAddress(label, district, detailed)
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DrovaPrimary),
                    modifier = Modifier.testTag("confirm_add_address_btn")
                ) {
                    Text(text = if (isAr) "حفظ العنوان" else "Save Address", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text(text = if (isAr) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun AddressItemRow(address: SavedAddress) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DrovaSurfaceVariant,
        border = BorderStroke(0.5.dp, DrovaBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = DrovaPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.labelAr,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DrovaTextPrimary,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = address.detailedAddressAr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DrovaTextSecondary,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = address.districtAr,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DrovaTextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RoleSwitchButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DrovaSurfaceVariant,
        border = BorderStroke(0.5.dp, DrovaBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DrovaPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DrovaTextPrimary,
                        fontSize = 13.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DrovaTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
