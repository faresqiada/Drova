package com.example.presentation.auth

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.BuildConfig
import com.example.core.designsystem.*
import com.example.domain.model.UserRole
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onOtpClick: () -> Unit,
    onChangeRoleClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedRole by authViewModel.selectedRole.collectAsState()
    val uiState by authViewModel.uiState.collectAsState()
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val focusManager = LocalFocusManager.current
    val activity = LocalActivity.current

    var phoneOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).user.role)
        }
    }

    val isFormValid = phoneOrEmail.isNotBlank() && password.length >= 4

    fun performLogin() {
        phoneError = if (phoneOrEmail.isBlank()) {
            if (isAr) "يرجى إدخال رقم الهاتف أو البريد" else "Please enter phone number or email"
        } else null

        passwordError = if (password.length < 4) {
            if (isAr) "كلمة المرور يجب أن لا تقل عن 4 خانات" else "Password must be at least 4 chars"
        } else null

        if (phoneError == null && passwordError == null) {
            focusManager.clearFocus()
            authViewModel.login(phoneOrEmail, password)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "تسجيل الدخول" else "Sign In",
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
                        .testTag("login_role_banner"),
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
                                text = if (isAr) "الدور النشط:" else "Active Role:",
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
                    text = if (isAr) "مرحباً بك في DROVA" else "Sign in to DROVA",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = DrovaCharcoal
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAr)
                        "سجّل الدخول لمتابعة حسابك كـ ${selectedRole.titleAr}"
                    else
                        "Access your ecosystem dashboard as ${selectedRole.titleEn}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DrovaTextSecondary,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Error banner
                AnimatedVisibility(visible = uiState is AuthUiState.Error) {
                    val error = uiState as? AuthUiState.Error
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = DrovaErrorContainer,
                        border = BorderStroke(1.dp, DrovaError.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isAr) error?.messageAr.orEmpty() else error?.messageEn.orEmpty(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DrovaErrorText,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Phone/Email Field
                DrovaTextField(
                    value = phoneOrEmail,
                    onValueChange = {
                        phoneOrEmail = it
                        if (phoneError != null) phoneError = null
                    },
                    label = if (isAr) "رقم الهاتف المحمول أو البريد" else "Phone Number or Email",
                    placeholder = if (isAr) "مثال: 01012345678" else "e.g. 01012345678",
                    leadingIcon = Icons.Default.Phone,
                    isError = phoneError != null,
                    errorMessage = phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    testTag = "login_phone_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password Field
                DrovaTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) passwordError = null
                    },
                    label = if (isAr) "كلمة المرور / الرمز السري" else "Password / PIN",
                    placeholder = if (isAr) "أدخل كلمة المرور" else "Enter password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = passwordError != null,
                    errorMessage = passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { performLogin() }
                    ),
                    testTag = "login_password_input"
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Login Button
                DrovaPrimaryButton(
                    text = if (isAr) "تسجيل الدخول" else "Sign In",
                    onClick = { performLogin() },
                    isLoading = uiState is AuthUiState.Loading,
                    enabled = isFormValid,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    testTag = "login_submit_btn"
                )
                Spacer(modifier = Modifier.height(10.dp))
                DrovaOutlinedButton(
                    text = if (isAr) "الدخول برمز OTP" else "Sign in with OTP",
                    onClick = onOtpClick,
                    enabled = uiState !is AuthUiState.Loading,
                    leadingIcon = Icons.Default.Phone,
                    testTag = "login_otp_btn"
                )
                Spacer(modifier = Modifier.height(12.dp))
                DrovaOutlinedButton(
                    text = if (isAr) "المتابعة باستخدام Google" else "Continue with Google",
                    onClick = {
                        activity?.let { authViewModel.signInWithGoogle(it) }
                    },
                    enabled = activity != null && uiState !is AuthUiState.Loading,
                    leadingIcon = Icons.Default.AccountCircle,
                    testTag = "login_google_btn"
                )
            }

            // Bottom Register Link
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
                        text = if (isAr) "ليس لديك حساب؟" else "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DrovaTextSecondary,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "إنشاء حساب جديد" else "Register Now",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DrovaTurquoise,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier
                            .clickable(onClick = onNavigateToRegister)
                            .testTag("login_goto_register_btn")
                    )
                }
            }
        }
    }
}
