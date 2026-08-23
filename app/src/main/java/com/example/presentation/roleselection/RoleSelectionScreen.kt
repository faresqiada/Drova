package com.example.presentation.roleselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.UserRole
import com.example.ui.theme.*

@Composable
fun RoleSelectionScreen(
    currentSelectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember(currentSelectedRole) {
        mutableStateOf(currentSelectedRole.takeUnless { it == UserRole.ADMIN } ?: UserRole.CUSTOMER)
    }
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("role_selection_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "اختيار نوع الحساب" else "Select Role",
                onBackClick = onBackClick
            )
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isAr) "ما هو دورك في منظومة DROVA؟" else "What is your role on DROVA?",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = DrovaCharcoal
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAr)
                        "اختر الحساب المناسب للوصول إلى الواجهة المخصصة لاحتياجاتك."
                    else
                        "Select the appropriate account to access your dedicated hub.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DrovaTextSecondary,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Role 1: Customer
                RoleOptionCard(
                    role = UserRole.CUSTOMER,
                    icon = Icons.Default.Person,
                    title = if (isAr) "عميل" else "Customer",
                    subtitle = if (isAr) "طلب وتوصيل طعام" else "Food Ordering & Tracking",
                    description = if (isAr)
                        "استكشف المطاعم، صمم طلبك، وتابع مراحل التوصيل التسع في الوقت الفعلي."
                    else
                        "Browse restaurants, customize meals & track live 9-stage order lifecycle.",
                    highlights = if (isAr)
                        listOf("تتبع مباشر لحظة بلحظة", "دفع نقدي أو محفظة ذكية", "عروض وخصومات فورية")
                    else
                        listOf("Real-time order tracking", "Cash or in-app wallet", "Exclusive partner perks"),
                    isSelected = selectedRole == UserRole.CUSTOMER,
                    onSelect = {
                        selectedRole = UserRole.CUSTOMER
                        onRoleSelected(UserRole.CUSTOMER)
                    },
                    testTag = "role_card_customer"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role 2: Restaurant
                RoleOptionCard(
                    role = UserRole.RESTAURANT,
                    icon = Icons.Default.Storefront,
                    title = if (isAr) "شريك مطعم" else "Restaurant Partner",
                    subtitle = if (isAr) "إدارة الطلبات والعمليات" else "Merchant Operations",
                    description = if (isAr)
                        "لوحة تحكم لإدارة الطلبات الواردة، مراحل المطبخ، والتسويات المالية المباشرة."
                    else
                        "Control incoming orders, kitchen prep queue, and instant financial settlements.",
                    highlights = if (isAr)
                        listOf("إدارة مراحل الطهي والتجهيز", "عمولة منصة تنافسية", "تسويات مالية فورية")
                    else
                        listOf("Kitchen preparation queue", "Low platform commission", "Direct payouts"),
                    isSelected = selectedRole == UserRole.RESTAURANT,
                    onSelect = {
                        selectedRole = UserRole.RESTAURANT
                        onRoleSelected(UserRole.RESTAURANT)
                    },
                    testTag = "role_card_restaurant"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role 3: Captain
                RoleOptionCard(
                    role = UserRole.CAPTAIN,
                    icon = Icons.Default.DeliveryDining,
                    title = if (isAr) "كابتن أسطول" else "Delivery Captain",
                    subtitle = if (isAr) "دخل مرن ونظام ورديات" else "Flexible & Shift Earnings",
                    description = if (isAr)
                        "انضم لأسطول DROVA مع حرية الاختيار بين العمل الحر (Free) أو الورديات (Shift)."
                    else
                        "Join DROVA fleet: choose on-demand Free Mode or guaranteed Shift Mode.",
                    highlights = if (isAr)
                        listOf("نظامي Shift Mode و Free Mode", "سحب فوري لأرباح المحفظة", "دعم فني مخصص للكباتن")
                    else
                        listOf("Free Mode vs Shift Mode", "Instant wallet withdrawals", "Dedicated support"),
                    isSelected = selectedRole == UserRole.CAPTAIN,
                    onSelect = {
                        selectedRole = UserRole.CAPTAIN
                        onRoleSelected(UserRole.CAPTAIN)
                    },
                    testTag = "role_card_captain"
                )
            }

            Column(modifier = Modifier.padding(top = 20.dp)) {
                DrovaPrimaryButton(
                    text = if (isAr) "المتابعة كـ ${selectedRole.titleAr}" else "Continue as ${selectedRole.titleEn}",
                    onClick = {
                        onRoleSelected(selectedRole)
                        onContinueClick()
                    },
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    testTag = "role_continue_btn"
                )
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    role: UserRole,
    icon: ImageVector,
    title: String,
    subtitle: String,
    description: String,
    highlights: List<String>,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String
) {
    val borderColor = if (isSelected) DrovaTurquoise else DrovaBorder
    val containerColor = if (isSelected) DrovaTurquoiseLight.copy(alpha = 0.35f) else DrovaSurface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) DrovaTurquoise else DrovaSurfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else DrovaCharcoal,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = DrovaTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Check indicator
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) DrovaTurquoise else DrovaSurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DrovaTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Highlight items
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                highlights.forEach { highlight ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(if (isSelected) DrovaTurquoise else DrovaTextMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = highlight,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) DrovaTextPrimary else DrovaTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
