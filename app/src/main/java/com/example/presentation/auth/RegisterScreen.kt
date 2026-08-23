package com.example.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.*
import com.example.domain.model.CaptainMode
import com.example.domain.model.UserRole
import com.example.ui.theme.*

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: (UserRole) -> Unit,
    onNavigateToLogin: () -> Unit,
    onChangeRoleClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedRole by authViewModel.selectedRole.collectAsState()
    val uiState by authViewModel.uiState.collectAsState()
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val focusManager = LocalFocusManager.current

    // Common fields
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Customer specific
    var customerCity by remember { mutableStateOf("القاهرة") }
    var customerDistrict by remember { mutableStateOf("المعادي") }

    // Restaurant specific
    var businessName by remember { mutableStateOf("") }
    var commercialRegister by remember { mutableStateOf("") }
    var restaurantAddress by remember { mutableStateOf("") }

    // Captain specific
    var nationalId by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("دراجة نارية (موتوسيكل)") }
    var captainMode by remember { mutableStateOf(CaptainMode.SHIFT_MODE) }

    var formError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess((uiState as AuthUiState.Success).user.role)
        }
    }

    fun submitRegistration() {
        focusManager.clearFocus()
        when (selectedRole) {
            UserRole.CUSTOMER -> {
                if (fullName.isBlank() || phone.isBlank()) {
                    formError = if (isAr) "يرجى كتابة الاسم ورقم الهاتف" else "Please enter your name and phone number"
                    return
                }
                formError = null
                authViewModel.registerCustomer(fullName, phone, customerCity, customerDistrict)
            }
            UserRole.RESTAURANT -> {
                if (businessName.isBlank() || fullName.isBlank() || phone.isBlank()) {
                    formError = if (isAr) "يرجى ملء جميع بيانات المطعم والمسؤول" else "Please complete all restaurant details"
                    return
                }
                formError = null
                authViewModel.registerRestaurant(businessName, fullName, phone, commercialRegister, restaurantAddress)
            }
            UserRole.CAPTAIN -> {
                if (fullName.isBlank() || phone.isBlank() || nationalId.isBlank()) {
                    formError = if (isAr) "يرجى ملء بيانات الكابتن والرقم القومي" else "Please enter all captain info and national ID"
                    return
                }
                formError = null
                authViewModel.registerCaptain(fullName, phone, nationalId, vehicleType, captainMode)
            }
            UserRole.ADMIN -> {
                formError = if (isAr) "يتم إنشاء حسابات المديرين من خلال لوحة الإدارة فقط" else "Admin accounts are created through the admin control layer only"
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("register_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "إنشاء حساب جديد" else "Create Account",
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Role header indicator
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onChangeRoleClick)
                        .testTag("register_role_banner"),
                    shape = RoundedCornerShape(8.dp),
                    color = DrovaSurface,
                    border = BorderStroke(1.dp, DrovaBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "نوع التسجيل:" else "Registering as:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DrovaTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DrovaRoleBadge(role = selectedRole)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "تغيير" else "Change",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DrovaTurquoise,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = DrovaTurquoise,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isAr) "انضم لمنظومة DROVA" else "Join the DROVA Network",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = DrovaCharcoal
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAr)
                        "املأ بياناتك لبدء استخدام حسابك كـ ${selectedRole.titleAr}"
                    else
                        "Complete your information to register as ${selectedRole.titleEn}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DrovaTextSecondary,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Error Message
                AnimatedVisibility(visible = formError != null || uiState is AuthUiState.Error) {
                    val msg = formError ?: (uiState as? AuthUiState.Error)?.let {
                        if (isAr) it.messageAr else it.messageEn
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = DrovaErrorContainer,
                        border = BorderStroke(1.dp, DrovaError.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = msg.orEmpty(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaErrorText,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Dynamic Fields based on Role
                when (selectedRole) {
                    UserRole.CUSTOMER -> {
                        DrovaTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = if (isAr) "الاسم بالكامل" else "Full Name",
                            placeholder = if (isAr) "مثال: أحمد مصطفى" else "e.g. Ahmed Mostafa",
                            leadingIcon = Icons.Default.Person,
                            testTag = "reg_customer_name"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = if (isAr) "رقم الهاتف المحمول" else "Phone Number",
                            placeholder = "010xxxxxxxx",
                            leadingIcon = Icons.Default.Phone,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            testTag = "reg_customer_phone"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DrovaTextField(
                                value = customerCity,
                                onValueChange = { customerCity = it },
                                label = if (isAr) "المحافظة / المدينة" else "City",
                                modifier = Modifier.weight(1f),
                                leadingIcon = Icons.Default.LocationCity,
                                testTag = "reg_customer_city"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            DrovaTextField(
                                value = customerDistrict,
                                onValueChange = { customerDistrict = it },
                                label = if (isAr) "المنطقة / الحي" else "District",
                                modifier = Modifier.weight(1f),
                                leadingIcon = Icons.Default.Place,
                                testTag = "reg_customer_district"
                            )
                        }
                    }

                    UserRole.RESTAURANT -> {
                        DrovaTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = if (isAr) "اسم المطعم / العلامة التجارية" else "Restaurant Brand Name",
                            placeholder = if (isAr) "مثال: شاورما الريم" else "e.g. Al Reem Shawarma",
                            leadingIcon = Icons.Default.Storefront,
                            testTag = "reg_restaurant_name"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = if (isAr) "اسم المدير أو المسؤول" else "Manager Name",
                            placeholder = if (isAr) "مثال: أحمد عبد الله" else "e.g. Ahmed Abdullah",
                            leadingIcon = Icons.Default.Person,
                            testTag = "reg_restaurant_manager"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = if (isAr) "رقم الهاتف للتواصل والطلبات" else "Business Contact Phone",
                            placeholder = "010xxxxxxxx",
                            leadingIcon = Icons.Default.Phone,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            testTag = "reg_restaurant_phone"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = commercialRegister,
                            onValueChange = { commercialRegister = it },
                            label = if (isAr) "رقم السجل التجاري أو البطاقة الضريبية" else "Commercial Registration / Tax ID",
                            placeholder = "CR-123456",
                            leadingIcon = Icons.Default.Badge,
                            testTag = "reg_restaurant_cr"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = restaurantAddress,
                            onValueChange = { restaurantAddress = it },
                            label = if (isAr) "عنوان الفرع الرئيسي" else "Branch Address",
                            placeholder = if (isAr) "شارع 9، المعادي، القاهرة" else "e.g. Road 9, Maadi, Cairo",
                            leadingIcon = Icons.Default.LocationOn,
                            testTag = "reg_restaurant_address"
                        )
                    }

                    UserRole.CAPTAIN -> {
                        DrovaTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = if (isAr) "الاسم الرباعي للكابتن" else "Captain Full Name",
                            placeholder = if (isAr) "مثال: محمود عادل السيد" else "e.g. Mahmoud Adel",
                            leadingIcon = Icons.Default.Person,
                            testTag = "reg_captain_name"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = if (isAr) "رقم الهاتف المحمول" else "Mobile Number",
                            placeholder = "011xxxxxxxx",
                            leadingIcon = Icons.Default.Phone,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            testTag = "reg_captain_phone"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = nationalId,
                            onValueChange = { nationalId = it },
                            label = if (isAr) "الرقم القومي (14 رقم)" else "National ID (14 digits)",
                            placeholder = "29XXXXXXXXXXXX",
                            leadingIcon = Icons.Default.Badge,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            testTag = "reg_captain_nid"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DrovaTextField(
                            value = vehicleType,
                            onValueChange = { vehicleType = it },
                            label = if (isAr) "نوع وسيلة التوصيل" else "Vehicle Type",
                            placeholder = if (isAr) "موتوسيكل / سكوتر / سيارة" else "Motorcycle / Scooter / Car",
                            leadingIcon = Icons.Default.TwoWheeler,
                            testTag = "reg_captain_vehicle"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Captain Mode Selection
                        Text(
                            text = if (isAr) "نظام العمل المفضل:" else "Preferred Working Mode:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DrovaTextPrimary,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { captainMode = CaptainMode.SHIFT_MODE }
                                    .testTag("reg_mode_shift"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (captainMode == CaptainMode.SHIFT_MODE) DrovaTurquoiseLight else DrovaSurface,
                                border = BorderStroke(
                                    if (captainMode == CaptainMode.SHIFT_MODE) 1.5.dp else 1.dp,
                                    if (captainMode == CaptainMode.SHIFT_MODE) DrovaTurquoise else DrovaBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isAr) "نظام الوردية" else "Shift Mode",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (captainMode == CaptainMode.SHIFT_MODE) DrovaTurquoiseHover else DrovaTextPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = if (isAr) "دخل أساسي مضمون + حوافز" else "Guaranteed pay + bonuses",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DrovaTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { captainMode = CaptainMode.FREE_MODE }
                                    .testTag("reg_mode_free"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (captainMode == CaptainMode.FREE_MODE) DrovaTurquoiseLight else DrovaSurface,
                                border = BorderStroke(
                                    if (captainMode == CaptainMode.FREE_MODE) 1.5.dp else 1.dp,
                                    if (captainMode == CaptainMode.FREE_MODE) DrovaTurquoise else DrovaBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isAr) "العمل الحر" else "Free Mode",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (captainMode == CaptainMode.FREE_MODE) DrovaTurquoiseHover else DrovaTextPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = if (isAr) "مرونة كاملة لكل رحلة" else "Full per-delivery freedom",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DrovaTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    UserRole.ADMIN -> {
                        Text(
                            text = if (isAr) "حسابات المديرين تُدار من خلال لوحة الإدارة فقط" else "Admin accounts are managed through the admin control layer only",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DrovaTextSecondary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                DrovaPrimaryButton(
                    text = if (isAr) "تأكيد التسجيل والدخول" else "Complete Registration & Enter",
                    onClick = { submitRegistration() },
                    isLoading = uiState is AuthUiState.Loading,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    testTag = "register_submit_btn"
                )
            }

            // Bottom Login Link
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isAr) "لديك حساب بالفعل؟" else "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DrovaTextSecondary,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "تسجيل الدخول" else "Sign In",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTurquoise,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier
                            .clickable(onClick = onNavigateToLogin)
                            .testTag("register_goto_login_btn")
                    )
                }
            }
        }
    }
}
