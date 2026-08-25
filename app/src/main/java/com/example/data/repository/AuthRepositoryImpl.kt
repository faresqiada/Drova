package com.example.data.repository

import android.app.Activity
import com.example.core.result.DrovaResult
import com.example.data.auth.FirebaseGoogleSignInManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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

    /**
     * Resolve authorization exclusively from Firestore /users/{FirebaseUID}.
     * A missing or unknown role is an authentication error, never a Customer fallback.
     */
    private suspend fun completeVerifiedFirebaseUser(
        firebaseUser: FirebaseUser,
        autoProvisionCustomer: Boolean = false
    ): AuthResult {
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

            val userDocument = FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseUser.uid)
            var document = userDocument.get().await()

            if (!document.exists() && autoProvisionCustomer) {
                val profile = hashMapOf<String, Any>("role" to "CUSTOMER")
                firebaseUser.email?.takeIf { it.isNotBlank() }?.let { profile["email"] = it }
                firebaseUser.displayName?.takeIf { it.isNotBlank() }?.let { profile["full_name"] = it }
                userDocument.set(profile).await()
                document = userDocument.get().await()
            }

            if (!document.exists()) {
                return AuthResult.Error(
                    messageAr = "لا يوجد ملف مستخدم موثق لهذا الحساب في DROVA.",
                    messageEn = "No verified DROVA user profile exists for this account."
                )
            }

            fun text(vararg keys: String): String? = keys.asSequence()
                .mapNotNull { key -> document.getString(key) }
                .firstOrNull { it.isNotBlank() }

            val roleValue = text("role")?.trim()?.uppercase()
            val role = when (roleValue) {
                "CUSTOMER" -> UserRole.CUSTOMER
                "CAPTAIN" -> UserRole.CAPTAIN
                "RESTAURANT" -> UserRole.RESTAURANT
                "ADMIN" -> UserRole.ADMIN
                else -> return AuthResult.Error(
                    messageAr = "دور الحساب غير معروف أو غير موثق في Firestore.",
                    messageEn = "The account role is missing or not recognized in Firestore."
                )
            }

            val user = User(
                id = firebaseUser.uid,
                fullName = text("full_name", "fullName")
                    ?: firebaseUser.displayName?.takeIf { it.isNotBlank() }
                    ?: firebaseUser.email?.substringBefore("@")?.ifBlank { null }
                    ?: "DROVA User",
                phone = text("phone") ?: firebaseUser.phoneNumber.orEmpty(),
                email = text("email") ?: firebaseUser.email.orEmpty(),
                role = role,
                city = text("city") ?: "القاهرة",
                district = text("district") ?: "المعادي",
                businessName = text("business_name", "businessName"),
                commercialRegister = text("commercial_register", "commercialRegister"),
                captainMode = when (text("captain_mode", "captainMode")?.uppercase()) {
                    "SHIFT_MODE" -> CaptainMode.SHIFT_MODE
                    else -> CaptainMode.FREE_MODE
                },
                isOnline = document.getBoolean("is_online")
                    ?: document.getBoolean("isOnline")
                    ?: true,
                vehicleType = text("vehicle_type", "vehicleType") ?: "دراجة نارية (موتوسيكل)"
            )

            sessionManager.setFirebaseUid(firebaseUser.uid)
            sessionManager.setAuthToken(idToken)
            sessionManager.setCurrentUser(user)
            AuthResult.Success(user)
        } catch (_: Exception) {
            AuthResult.Error(
                messageAr = "تعذر قراءة ملف الدور من Firestore. لا يمكن فتح لوحة غير موثقة.",
                messageEn = "Could not read the role profile from Firestore. An unverified dashboard cannot be opened."
            )
        }
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

        return completeVerifiedFirebaseUser(firebaseUser, autoProvisionCustomer = true)
    }

    override suspend fun completePhoneSignIn(firebaseUser: FirebaseUser): AuthResult {
        return completeVerifiedFirebaseUser(firebaseUser)
    }

    private fun firebaseEmailLoginError(error: Exception): AuthResult.Error {
        val code = (error as? FirebaseAuthException)?.errorCode.orEmpty()
        return when (code) {
            "ERROR_INVALID_EMAIL" -> AuthResult.Error(
                messageAr = "صيغة البريد الإلكتروني غير صحيحة.",
                messageEn = "The email address format is invalid."
            )
            "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> AuthResult.Error(
                messageAr = "البريد الإلكتروني أو كلمة المرور غير صحيحة في Firebase.",
                messageEn = "The Firebase email or password is incorrect."
            )
            "ERROR_USER_DISABLED" -> AuthResult.Error(
                messageAr = "حساب Firebase هذا معطل.",
                messageEn = "This Firebase account is disabled."
            )
            "ERROR_NETWORK_REQUEST_FAILED" -> AuthResult.Error(
                messageAr = "تعذر الاتصال بـ Firebase. تحقق من الإنترنت وحاول مرة أخرى.",
                messageEn = "Could not reach Firebase. Check the internet connection and try again."
            )
            "ERROR_TOO_MANY_REQUESTS" -> AuthResult.Error(
                messageAr = "تم إيقاف المحاولات مؤقتًا بسبب كثرة المحاولات. حاول لاحقًا.",
                messageEn = "Too many attempts. Please try again later."
            )
            else -> AuthResult.Error(
                messageAr = if (code.isBlank())
                    "فشل تسجيل الدخول عبر Firebase. تحقق من بيانات الحساب وحاول مرة أخرى."
                else
                    "فشل تسجيل الدخول عبر Firebase ($code).",
                messageEn = if (code.isBlank())
                    "Firebase sign-in failed. Verify the account credentials and try again."
                else
                    "Firebase sign-in failed ($code)."
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

        // Email accounts authenticate with Firebase first; role is resolved from Firestore by UID.
        if (trimmed.contains("@")) {
            try {
                val firebaseUser = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(trimmed, pinOrPassword)
                    .await()
                    .user
                    ?: return AuthResult.Error(
                        messageAr = "تعذر العثور على حساب Firebase.",
                        messageEn = "Firebase account was not found."
                    )
                return completeVerifiedFirebaseUser(firebaseUser)
            } catch (error: Exception) {
                // Do not fall through to the legacy server for email accounts. That would
                // hide the real Firebase error and could produce a misleading login result.
                return firebaseEmailLoginError(error)
            }
        }

        // Non-email legacy credentials may still authenticate through the old API.
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
