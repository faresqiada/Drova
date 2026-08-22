package com.example.data.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleSignInException(
    val messageAr: String,
    val messageEn: String,
    cause: Throwable? = null
) : Exception(messageEn, cause)

class FirebaseGoogleSignInManager(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun signIn(activity: Activity): Result<FirebaseUser> {
        return try {
            val serverClientId = activity.getString(com.example.R.string.default_web_client_id)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val credentialManager = CredentialManager.create(activity)
            val credentialResponse = credentialManager.getCredential(activity, request)
            val credential = credentialResponse.credential

            if (
                credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return Result.failure(
                    GoogleSignInException(
                        messageAr = "نوع بيانات تسجيل الدخول من Google غير مدعوم",
                        messageEn = "Unsupported Google credential type"
                    )
                )
            }

            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(
                    GoogleSignInException(
                        messageAr = "تعذر إنشاء مستخدم Firebase",
                        messageEn = "Firebase did not return an authenticated user"
                    )
                )
            Result.success(firebaseUser)
        } catch (error: GetCredentialCancellationException) {
            Result.failure(
                GoogleSignInException(
                    messageAr = "تم إلغاء تسجيل الدخول بواسطة المستخدم",
                    messageEn = "Google Sign-In was cancelled",
                    cause = error
                )
            )
        } catch (error: FirebaseAuthInvalidCredentialsException) {
            Result.failure(
                GoogleSignInException(
                    messageAr = "بيانات اعتماد Google غير صالحة أو منتهية.",
                    messageEn = "The Google credential is invalid or expired.",
                    cause = error
                )
            )
        } catch (error: FirebaseAuthException) {
            if (error.errorCode == FirebaseAuth.ERROR_NETWORK_REQUEST_FAILED) {
                Result.failure(
                    GoogleSignInException(
                        messageAr = "تعذر الاتصال بخدمة Firebase. تحقق من اتصال الإنترنت وحاول مرة أخرى.",
                        messageEn = "Could not reach Firebase. Check your internet connection and try again.",
                        cause = error
                    )
                )
            } else {
                Result.failure(
                    GoogleSignInException(
                        messageAr = "فشل تسجيل الدخول عبر Firebase. حاول مرة أخرى.",
                        messageEn = "Firebase authentication failed. Please try again.",
                        cause = error
                    )
                )
            }
        } catch (error: GetCredentialException) {
            Result.failure(
                GoogleSignInException(
                    messageAr = "تعذر بدء تسجيل الدخول عبر Google. حاول مرة أخرى.",
                    messageEn = "Google Sign-In could not be started. Please try again.",
                    cause = error
                )
            )
        } catch (error: Exception) {
            Result.failure(
                GoogleSignInException(
                    messageAr = "حدث خطأ غير متوقع أثناء تسجيل الدخول عبر Google.",
                    messageEn = "An unexpected error occurred during Google Sign-In.",
                    cause = error
                )
            )
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
