package com.example.data.repository

import android.app.Activity
import com.example.core.result.DrovaResult
import com.example.data.auth.FirebaseGoogleSignInManager
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

    override fun quickSwitchRole(role: UserRole) {
        sessionManager.setSelectedRole(role)
        val user = when (role) {
            UserRole.CUSTOMER -> User(
                id = "cust_1",
                fullName = "أحمد مصطفى",
                phone = "+201012345678",
                email = "ahmed@drova.eg",
                role = UserRole.CUSTOMER,
                city = "القاهرة",
                district = "المعادي"
            )
            UserRole.RESTAURANT -> User(
                id = "rest_1",
                fullName = "إدارة شاورما الريم",
                phone = "+201023456789",
                email = "partner@alreem.eg",
                role = UserRole.RESTAURANT,
                city = "القاهرة",
                district = "المعادي",
                businessName = "شاورما الريم المعادي",
                commercialRegister = "CR-98421-EG"
            )
            UserRole.CAPTAIN -> User(
                id = "cap_1",
                fullName = "محمود عادل",
                phone = "+201198765432",
                email = "mahmoud.captain@drova.eg",
                role = UserRole.CAPTAIN,
                city = "القاهرة",
                district = "المعادي / التجمع",
                captainMode = CaptainMode.SHIFT_MODE,
                isOnline = true,
                vehicleType = "دراجة نارية (موتوسيكل)"
            )
        }
        sessionManager.setCurrentUser(user)
        sessionManager.setAuthToken("token_demo_${role.name.lowercase()}")
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
            val idToken = firebaseUser.getIdToken(false).await()?.token
                ?: return AuthResult.Error(
                    messageAr = "تعذر الحصول على جلسة Firebase صالحة.",
                    messageEn = "Could not obtain a valid Firebase session."
                )
            val role = selectedRole.value
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

        delay(300) // Smooth UX

        val role = sessionManager.selectedRole.value
        val user = when (role) {
            UserRole.CUSTOMER -> User(
                id = "cust_1",
                fullName = "أحمد مصطفى",
                phone = if (trimmed.startsWith("+")) trimmed else "+20$trimmed",
                email = if (trimmed.contains("@")) trimmed else "ahmed.customer@drova.eg",
                role = UserRole.CUSTOMER,
                city = "القاهرة",
                district = "المعادي"
            )
            UserRole.RESTAURANT -> User(
                id = "rest_1",
                fullName = "مدير شاورما الريم",
                phone = if (trimmed.startsWith("+")) trimmed else "+20$trimmed",
                email = if (trimmed.contains("@")) trimmed else "manager@alreem.eg",
                role = UserRole.RESTAURANT,
                city = "القاهرة",
                district = "المعادي",
                businessName = "شاورما الريم المعادي",
                commercialRegister = "CR-98421-EG"
            )
            UserRole.CAPTAIN -> User(
                id = "cap_1",
                fullName = "محمود عادل",
                phone = if (trimmed.startsWith("+")) trimmed else "+20$trimmed",
                email = "captain.mahmoud@drova.eg",
                role = UserRole.CAPTAIN,
                city = "القاهرة",
                district = "المعادي / التجمع",
                captainMode = CaptainMode.SHIFT_MODE,
                isOnline = true,
                vehicleType = "دراجة نارية (موتوسيكل)"
            )
        }

        sessionManager.setCurrentUser(user)
        sessionManager.setAuthToken("token_session_${System.currentTimeMillis()}")
        return AuthResult.Success(user)
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

        delay(300)
        val user = User(
            id = "cust_${System.currentTimeMillis() % 10000}",
            fullName = fullName.trim(),
            phone = phone.trim(),
            role = UserRole.CUSTOMER,
            city = city.ifBlank { "القاهرة" },
            district = district.ifBlank { "المعادي" }
        )
        sessionManager.setCurrentUser(user)
        sessionManager.setAuthToken("token_customer_${System.currentTimeMillis()}")
        return AuthResult.Success(user)
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

        delay(300)
        val user = User(
            id = "rest_${System.currentTimeMillis() % 10000}",
            fullName = managerName.trim(),
            phone = phone.trim(),
            role = UserRole.RESTAURANT,
            businessName = businessName.trim(),
            commercialRegister = commercialRegister.trim().ifBlank { "CR-PENDING" },
            district = address.trim().ifBlank { "القاهرة" }
        )
        sessionManager.setCurrentUser(user)
        sessionManager.setAuthToken("token_restaurant_${System.currentTimeMillis()}")
        return AuthResult.Success(user)
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

        delay(300)
        val user = User(
            id = "cap_${System.currentTimeMillis() % 10000}",
            fullName = fullName.trim(),
            phone = phone.trim(),
            role = UserRole.CAPTAIN,
            captainMode = captainMode,
            vehicleType = vehicleType.ifBlank { "دراجة نارية (موتوسيكل)" },
            isOnline = true
        )
        sessionManager.setCurrentUser(user)
        sessionManager.setAuthToken("token_captain_${System.currentTimeMillis()}")
        return AuthResult.Success(user)
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
