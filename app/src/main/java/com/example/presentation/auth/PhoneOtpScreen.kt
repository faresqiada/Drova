package com.example.presentation.auth

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.DrovaOutlinedButton
import com.example.core.designsystem.DrovaPrimaryButton
import com.example.core.designsystem.DrovaTextField
import com.example.core.designsystem.DrovaTopBar
import com.example.core.designsystem.AppLanguage
import com.example.core.designsystem.DrovaLanguageManager
import com.example.domain.model.UserRole
import com.example.ui.theme.DrovaBackground
import com.example.ui.theme.DrovaTextPrimary
import com.example.ui.theme.DrovaTextSecondary
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

@Composable
fun PhoneOtpScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (UserRole) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = DrovaLanguageManager.currentLanguage == AppLanguage.ARABIC
    val activity = LocalActivity.current
    val uiState by authViewModel.uiState.collectAsState()
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is AuthUiState.Success) onLoginSuccess(state.user.role)
    }

    fun sendCode(forceResend: Boolean = false) {
        val normalized = phone.trim().let { value ->
            when {
                value.startsWith("+") -> value
                value.startsWith("0") -> "+20${value.drop(1)}"
                else -> "+20$value"
            }
        }
        if (activity == null) {
            localError = if (isAr) "لا يمكن بدء التحقق من هذا السياق." else "Phone verification is unavailable in this context."
            return
        }
        if (normalized.length < 10) {
            localError = if (isAr) "أدخل رقم هاتف صحيحًا." else "Enter a valid phone number."
            return
        }
        localError = null
        sending = true
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        sending = false
                        if (task.isSuccessful) {
                            task.result?.user?.let { authViewModel.completePhoneSignIn(it) }
                        } else {
                            localError = task.exception?.localizedMessage ?: if (isAr) "فشل التحقق." else "Verification failed."
                        }
                    }
            }

            override fun onVerificationFailed(error: FirebaseException) {
                sending = false
                localError = error.localizedMessage ?: if (isAr) "تعذر إرسال رمز OTP." else "Could not send OTP."
            }

            override fun onCodeSent(
                id: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = id
                resendToken = token
                sending = false
                localError = null
            }
        }
        val optionsBuilder = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(normalized)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
        if (forceResend && resendToken != null) optionsBuilder.setForceResendingToken(resendToken!!)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    fun verifyCode() {
        val id = verificationId
        if (id.isNullOrBlank() || code.trim().length < 6) {
            localError = if (isAr) "أدخل رمز التحقق المكوّن من 6 أرقام." else "Enter the 6-digit verification code."
            return
        }
        sending = true
        val credential = PhoneAuthProvider.getCredential(id, code.trim())
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                sending = false
                if (task.isSuccessful) {
                    task.result?.user?.let { authViewModel.completePhoneSignIn(it) }
                } else {
                    localError = task.exception?.localizedMessage ?: if (isAr) "رمز التحقق غير صحيح." else "Invalid verification code."
                }
            }
    }

    androidx.compose.material3.Scaffold(
        modifier = modifier.fillMaxSize().testTag("phone_otp_screen"),
        topBar = {
            DrovaTopBar(
                title = if (isAr) "التحقق برقم الهاتف" else "Phone Verification",
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
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = if (isAr) "تسجيل دخول حقيقي باستخدام OTP" else "Real OTP sign-in",
                color = DrovaTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.padding(6.dp))
            Text(
                text = if (isAr) "سيتم إرسال رمز مؤقت من Firebase إلى هاتفك. لا نستخدم رمزًا تجريبيًا." else "Firebase will send a temporary code to your phone. No demo code is used.",
                color = DrovaTextSecondary
            )
            Spacer(Modifier.padding(14.dp))
            DrovaTextField(
                value = phone,
                onValueChange = { phone = it; localError = null },
                label = if (isAr) "رقم الهاتف" else "Phone number",
                placeholder = if (isAr) "01012345678" else "+201012345678",
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                testTag = "otp_phone_input"
            )
            Spacer(Modifier.padding(8.dp))
            DrovaPrimaryButton(
                text = if (verificationId == null) (if (isAr) "إرسال رمز OTP" else "Send OTP") else (if (isAr) "إعادة إرسال الرمز" else "Resend OTP"),
                onClick = { sendCode(verificationId != null) },
                enabled = !sending,
                isLoading = sending,
                leadingIcon = Icons.Default.Security,
                testTag = "otp_send_button"
            )
            if (verificationId != null) {
                Spacer(Modifier.padding(12.dp))
                DrovaTextField(
                    value = code,
                    onValueChange = { code = it.filter(Char::isDigit).take(6); localError = null },
                    label = if (isAr) "رمز التحقق" else "Verification code",
                    placeholder = "123456",
                    leadingIcon = Icons.Default.Security,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    testTag = "otp_code_input"
                )
                Spacer(Modifier.padding(8.dp))
                DrovaPrimaryButton(
                    text = if (isAr) "تأكيد الرمز وتسجيل الدخول" else "Verify and sign in",
                    onClick = ::verifyCode,
                    enabled = code.length == 6 && !sending,
                    isLoading = sending,
                    testTag = "otp_verify_button"
                )
            }
            if (!localError.isNullOrBlank()) {
                Spacer(Modifier.padding(8.dp))
                Text(text = localError.orEmpty(), color = androidx.compose.ui.graphics.Color.Red)
            }
            Spacer(Modifier.padding(16.dp))
            DrovaOutlinedButton(
                text = if (isAr) "العودة لتسجيل الدخول بكلمة مرور" else "Back to password sign-in",
                onClick = onBackClick,
                testTag = "otp_back_button"
            )
        }
    }
}

