package com.example.data.repository

import android.app.Activity
import com.example.BuildConfig
import com.example.core.result.DrovaResult
import com.example.data.auth.FirebaseGoogleSignInManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.data.auth.GoogleSignInException
import com.example.data.local.source.SessionManager
import com.example.data.remote.dto.*
import com.example.data.remote.source.AuthRemoteDataSource
import com.example.domain.model.CaptainMode
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Locale

class AuthRepositoryImpl(
    private val sessionManager: SessionManager = SessionManager(),
    private val remoteDataSource: AuthRemoteDataSource? = null,
    private val googleSignInManager: FirebaseGoogleSignInManager = FirebaseGoogleSignInManager()
) : AuthRepository {

    override val selectedRole: StateFlow<UserRole> = sessionManager.selectedRole
    override val currentUser: StateFlow<User?> = sessionManager.currentUser
    override val firebaseUid: StateFlow<String?> = sessionManager.firebaseUid

    override fun setSelectedRole(role: UserRole) {
        sessionManager.setSelectedRole(role)
    }

    override suspend fun hasAdminClaim(): Boolean = try {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return false
        firebaseUser.getIdToken(false).await()?.claims?.get("admin") == true
    } catch (_: Exception) {
        false
    }

    override fun quickSwitchRole(role: UserRole) {
        // Role switching must come from a verified authenticated session, never from demo data.
        return
    }

    override suspend fun signInWithGoogle(activity: Activity): AuthResult {
        val result = googleSignInManager.signIn(activity)
        val firebaseUser = result.getOrElse { error ->
            val signInError = error as? GoogleSignInException
            return AuthResult.Error(
                messageAr = signInError?.messageAr ?: "فشل تسجيل الدخول عبر Google. حاول مرة أخرى.",
                messageEn = signInError?.messageEn ?: "Google Sign-In failed. Please try again."
            )
        }

        return try {
            val tokenResult = firebaseUser.getIdToken(false).await()
                ?: return AuthResult.Error(
                    messageAr = "تعذر الحصول على جلسة Firebase صالحة.",
                    messageEn = "Could not obtain a valid Firebase session."
                )
            val idToken = tokenResult.token
                ?: return AuthResult.Error(
                    messageAr = "تعذر الحصول على رمز Firebase صالح.",
                    messageEn = "Could not obtain a valid Firebase token."
                )
            val isAdminClaim = tokenResult.claims["admin"] == true
            val claimedRole = if (isAdminClaim) {
                UserRole.ADMIN
            } else {
                (tokenResult.claims["role"] as? String)
                    ?.uppercase(Locale.ROOT)
                    ?.let { value -> UserRole.values().firstOrNull { it != UserRole.ADMIN && it.name == value } }
            }
            // Verified Firebase users default to customer access; elevated roles require claims.
            val role = claimedRole ?: UserRole.CUSTOMER
            val user = User(
                id = firebaseUser.uid,
                fullName = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                    ?: firebaseUser.email?.substringBefore("@")?.ifBlank { null }
                    ?: "DROVA User",
                phone = firebaseUser.phoneNumber.orEmpty(),
                email = firebaseUser.email.orEmpty(),
                role = role,
                city = "القاهرة",
                district = "المعادي"
            )
            sessionManager.setFirebaseUid(firebaseUser.uid)
            sessionManager.setAuthToken(idToken)
            sessionManager.setCurrentUser(user)
            AuthResult.Success(user)
        } catch (error: Exception) {
            AuthResult.Error(
                messageAr = "تعذر إكمال جلسة Firebase. تحقق من اتصال الإنترنت وحاول مرة أخرى.",
                messageEn = "Could not complete the Firebase session. Check your internet connection and try again."
            )
        }
    }

    override suspend fun completePhoneSignIn(firebaseUser: FirebaseUser): AuthResult {
        return try {
            val tokenResult = firebaseUser.getIdToken(false).await()
                ?: return AuthResult.Error(
                    messageAr = "تعذر الحصول على جلسة Firebase صالحة.",
                    messageEn = "Could not obtain a valid Firebase session."
                )
            val idToken = tokenResult.token
                ?: return AuthResult.Error(
                    messageAr = "تعذر الحصول على رمز Firebase صالح.",
                    messageEn = "Could not obtain a valid Firebase token."
                )
            val isAdminClaim = tokenResult.claims["admin"] == true
            val claimedRole = if (isAdminClaim) {
                UserRole.ADMIN
            } else {
                (tokenResult.claims["role"] as? String)
                    ?.uppercase(Locale.ROOT)
                    ?.let { value -> UserRole.values().firstOrNull { it != UserRole.ADMIN && it.name == value } }
            }
            // Verified Firebase users default to customer access; elevated roles require claims.
            val role = claimedRole ?: UserRole.CUSTOMER
            val user = User(
                id = firebaseUser.uid,
                fullName = firebaseUser.displayName?.takeIf { it.isNotBlank() } ?: "DROVA User",
                phone = firebaseUser.phoneNumber.orEmpty(),
                email = firebaseUser.email.orEmpty(),
                role = role,
                city = "القاهرة",
                district = "المعادي"
            )
            sessionManager.setFirebaseUid(firebaseUser.uid)
            sessionManager.setAuthToken(idToken)
            sessionManager.setCurrentUser(user)
            AuthResult.Success(user)
        } catch (_: Exception) {
            AuthResult.Error(
                messageAr = "تعذر إكمال تسجيل الدخول برقم الهاتف.",
                messageEn = "Could not complete phone sign-in."
            )
        }
    }

    override suspend fun login(phoneOrEmail: String, pinOrPassword: String): AuthResult {
        val trimmed = phoneOrEmail.trim()
        if (trimmed.isEmpty()) {
            return AuthResult.Error(
                messageAr = "يرجى إدخال رقم الهاتف أو البريد الإلكتروني",
                messageEn = "Please enter your phone number or email"
            )
        }
        if (pinOrPassword.length < 4) {
            return AuthResult.Error(
                messageAr = "كلمة المرور يجب أن لا تقل عن 4 رموز",
                messageEn = "Password must be at least 4 characters"
            )
        }

        // Try Remote Auth first if available
        remoteDataSource?.let { remote ->
            when (val remoteResult = remote.login(LoginRequestDto(phoneOrEmail = trimmed, password = pinOrPassword, role = selectedRole.value.name))) {
                is DrovaResult.Success -> {
                    val userDto = remoteResult.data.user
                    val token = remoteResult.data.token
                    if (userDto != null) {
                        val domainUser = userDto.toDomain()
                        if (domainUser.role == UserRole.ADMIN && !hasAdminClaim()) {
                            return AuthResult.Error(
                                messageAr = "لا يمكن فتح جلسة Admin بدون Firebase custom claim.",
                                messageEn = "An Admin session requires a verified Firebase custom claim."
                            )
                        }
                        FirebaseAuth.getInstance().currentUser?.uid?.let(sessionManager::setFirebaseUid)
                        sessionManager.setCurrentUser(domainUser)
                        sessionManager.setAuthToken(token)
                        return AuthResult.Success(domainUser)
                    }
                }
                is DrovaResult.Error -> {
                    // Fall back to local authentication
                }
                DrovaResult.Loading -> {}
            }
        }

        return AuthResult.Error(
            messageAr = "تعذر تسجيل الدخول من الخادم. استخدم OTP أو تحقق من بيانات الحساب.",
            messageEn = "Server authentication failed. Use OTP or verify the account credentials."
        )
    }

    override suspend fun registerCustomer(
        fullName: String,
        phone: String,
        city: String,
        district: String
    ): AuthResult {
        if (fullName.isBlank() || phone.isBlank()) {
            return AuthResult.Error("يرجى ملء جميع الحقول المطلوبة", "Please fill all required fields")
        }

        remoteDataSource?.let { remote ->
            when (val remoteResult = remote.registerCustomer(
                CustomerRegisterRequestDto(fullName = fullName, phone = phone, city = city, district = district)
            )) {
                is DrovaResult.Success -> {
                    remoteResult.data.user?.let { userDto ->
                        val user = userDto.toDomain()
                        sessionManager.setCurrentUser(user)
                        sessionManager.setAuthToken(remoteResult.data.token)
                        return AuthResult.Success(user)
                    }
                }
                is DrovaResult.Error -> {}
                DrovaResult.Loading -> {}
            }
        }

        return AuthResult.Error(
            messageAr = "تعذر إنشاء الحساب من الخادم. لا يمكن استخدام تسجيل تجريبي.",
            messageEn = "Server registration failed. Demo registration is disabled."
        )
    }

    override suspend fun registerRestaurant(
        businessName: String,
        managerName: String,
        phone: String,
        commercialRegister: String,
        address: String
    ): AuthResult {
        if (businessName.isBlank() || managerName.isBlank() || phone.isBlank()) {
            return AuthResult.Error("يرجى إدخال اسم المطعم والمسؤول ورقم الهاتف", "Please fill required restaurant details")
        }

        remoteDataSource?.let { remote ->
            when (val remoteResult = remote.registerRestaurant(
                RestaurantRegisterRequestDto(
                    businessName = businessName,
                    managerName = managerName,
                    phone = phone,
                    commercialRegister = commercialRegister,
                    address = address
                )
            )) {
                is DrovaResult.Success -> {
                    remoteResult.data.user?.let { userDto ->
                        val user = userDto.toDomain()
                        sessionManager.setCurrentUser(user)
                        sessionManager.setAuthToken(remoteResult.data.token)
                        return AuthResult.Success(user)
                    }
                }
                is DrovaResult.Error -> {}
                DrovaResult.Loading -> {}
            }
        }

        return AuthResult.Error(
            messageAr = "تعذر إنشاء حساب المطعم من الخادم. لا يمكن استخدام تسجيل تجريبي.",
            messageEn = "Server restaurant registration failed. Demo registration is disabled."
        )
    }

    override suspend fun registerCaptain(
        fullName: String,
        phone: String,
        nationalId: String,
        vehicleType: String,
        captainMode: CaptainMode
    ): AuthResult {
        if (fullName.isBlank() || phone.isBlank() || nationalId.isBlank()) {
            return AuthResult.Error("يرجى إدخال الاسم ورقم الهاتف والرقم القومي", "Please fill captain registration details")
        }

        remoteDataSource?.let { remote ->
            when (val remoteResult = remote.registerCaptain(
                CaptainRegisterRequestDto(
                    fullName = fullName,
                    phone = phone,
                    nationalId = nationalId,
                    vehicleType = vehicleType,
                    captainMode = captainMode.name
                )
            )) {
                is DrovaResult.Success -> {
                    remoteResult.data.user?.let { userDto ->
                        val user = userDto.toDomain()
                        sessionManager.setCurrentUser(user)
                        sessionManager.setAuthToken(remoteResult.data.token)
                        return AuthResult.Success(user)
                    }
                }
                is DrovaResult.Error -> {}
                DrovaResult.Loading -> {}
            }
        }

        return AuthResult.Error(
            messageAr = "تعذر تسجيل الكابتن من الخادم. لا يمكن استخدام تسجيل تجريبي.",
            messageEn = "Server captain registration failed. Demo registration is disabled."
        )
    }

    override suspend fun logout() {
        googleSignInManager.signOut()
        remoteDataSource?.let {
            try { it.logout() } catch (e: Exception) {}
        }
        delay(150)
        sessionManager.clearSession()
    }
}
