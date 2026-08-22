package com.example.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("welcome_screen"),
        topBar = {
            DrovaSurface(
                style = DrovaSurfaceStyle.FLAT,
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DrovaMark(size = 28.dp, tint = DrovaDeep, innerTint = DrovaPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "DROVA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = DrovaDeep,
                                fontSize = 18.sp
                            )
                        )
                    }

                    DrovaLanguageToggle()
                }
            }
        },
        containerColor = DrovaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp, bottom = 24.dp)
            ) {
                // Platform Tag
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DrovaDeep,
                    modifier = Modifier.testTag("welcome_badge")
                ) {
                    Text(
                        text = if (isAr) "المنظومة التجارية الذكية في مصر" else "EGYPT COMMERCIAL DELIVERY ECOSYSTEM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline
                Text(
                    text = if (isAr)
                        "بنية تحتية متطورة لربط\nالعميل والمطعم والكابتن"
                    else
                        "Advanced Infrastructure for\nCustomer, Restaurant & Captain",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        lineHeight = 34.sp,
                        letterSpacing = 0.sp,
                        color = DrovaDeep
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Value description
                Text(
                    text = if (isAr)
                        "منصة توصيل تجارية حديثة تضمن السرعة الفائقة، الشراكة العادلة للمطاعم، ونظام دخل مرن للكباتن."
                    else
                        "High-speed delivery operations, fair merchant settlements, and flexible fleet income.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DrovaTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 21.sp,
                        letterSpacing = 0.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 3 Structured Ecosystem Pillars
                EcosystemPillar(
                    index = "01",
                    icon = Icons.Default.ShoppingBag,
                    title = if (isAr) "تجربة طلب متكاملة للعملاء" else "Seamless Customer Ordering",
                    description = if (isAr) "تتبع دقيق لمراحل الطلب التسع من المطبخ وحتى باب منزلك" else "Real-time 9-stage order tracking from kitchen to doorstep",
                    accentColor = DrovaPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                EcosystemPillar(
                    index = "02",
                    icon = Icons.Default.Storefront,
                    title = if (isAr) "شراكة عادلة للمطاعم" else "Fair Restaurant Partner Hub",
                    description = if (isAr) "عمولات تنافسية، تسويات مالية فورية، وإدارة شاملة للطلبات" else "Competitive commissions, direct settlements & smart menu ops",
                    accentColor = DrovaDeep
                )

                Spacer(modifier = Modifier.height(10.dp))

                EcosystemPillar(
                    index = "03",
                    icon = Icons.Default.ElectricMoped,
                    title = if (isAr) "أسطول كباتن مرن وعالي العائد" else "High-Yield Captain Fleet",
                    description = if (isAr) "حرية الاختيار بين العمل الحر أو الورديات المضمونة مع محفظة فورية" else "Choose between Free Mode or Shift Mode with instant wallet payouts",
                    accentColor = DrovaTurquoiseHover
                )
            }

            // CTAs at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                DrovaPrimaryButton(
                    text = if (isAr) "ابدأ الآن - اختر دورك" else "Get Started - Select Role",
                    onClick = onGetStartedClick,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    testTag = "welcome_get_started_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                DrovaOutlinedButton(
                    text = if (isAr) "تسجيل الدخول لحسابك" else "Log In to Existing Account",
                    onClick = onLoginClick,
                    testTag = "welcome_login_btn"
                )
            }
        }
    }
}

@Composable
private fun EcosystemPillar(
    index: String,
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    DrovaSurface(
        modifier = Modifier.fillMaxWidth(),
        style = DrovaSurfaceStyle.FLAT,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DrovaSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = index,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaPrimary,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTextPrimary,
                            fontSize = 13.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DrovaTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
