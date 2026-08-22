package com.example.core.network

import com.example.core.result.DrovaError
import com.example.core.result.DrovaResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safe API wrapper that intercepts all network, serialization, and HTTP exceptions
 * and normalizes them into strongly-typed DrovaResult.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): DrovaResult<T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall()
            DrovaResult.Success(response)
        } catch (e: SocketTimeoutException) {
            DrovaResult.Error(
                error = DrovaError.Network.Timeout,
                messageAr = "انتهت مهلة الاتصال بالخادم، يرجى المحاولة مرة أخرى",
                messageEn = "Connection timed out. Please try again.",
                cause = e
            )
        } catch (e: UnknownHostException) {
            DrovaResult.Error(
                error = DrovaError.Network.NoInternet,
                messageAr = "لا يوجد اتصال بالإنترنت، يرجى التحقق من اتصالك",
                messageEn = "No internet connection detected. Please check your network.",
                cause = e
            )
        } catch (e: ConnectException) {
            DrovaResult.Error(
                error = DrovaError.Network.NoInternet,
                messageAr = "تعذر الوصول إلى خوادم DROVA، يرجى المحاولة لاحقاً",
                messageEn = "Could not reach DROVA servers. Please try again later.",
                cause = e
            )
        } catch (e: HttpException) {
            val code = e.code()
            val error = when (code) {
                401 -> DrovaError.Network.Unauthorized(code)
                403 -> DrovaError.Network.Forbidden(code)
                404 -> DrovaError.Network.NotFound(code)
                422 -> DrovaError.Network.ValidationError(code)
                in 500..599 -> DrovaError.Network.ServerError(code)
                else -> DrovaError.Network.Unknown("HTTP $code")
            }

            val (msgAr, msgEn) = when (code) {
                401 -> "انتهت صلاحية الجلسة، يرجى تسجيل الدخول مجدداً" to "Session expired. Please log in again."
                403 -> "ليس لديك صلاحية لتنفيذ هذا الإجراء" to "You do not have permission for this action."
                404 -> "العنصر المطلوب غير موجود" to "The requested resource was not found."
                422 -> "البيانات المدخلة غير صحيحة" to "Validation failed for the submitted data."
                in 500..599 -> "حدث خطأ في خادم DROVA، يرجى المحاولة لاحقاً" to "DROVA server error. Please try again later."
                else -> "حدث خطأ غير متوقع ($code)" to "Unexpected error ($code)."
            }

            DrovaResult.Error(
                error = error,
                messageAr = msgAr,
                messageEn = msgEn,
                cause = e
            )
        } catch (e: IOException) {
            DrovaResult.Error(
                error = DrovaError.Network.Unknown(e.message),
                messageAr = "حدث خطأ أثناء نقل البيانات عبر الشبكة",
                messageEn = "Network I/O error occurred.",
                cause = e
            )
        } catch (e: Exception) {
            DrovaResult.Error(
                error = DrovaError.Network.Unknown(e.message),
                messageAr = "حدث خطأ غير متوقع: ${e.localizedMessage ?: "يرجى المحاولة"}",
                messageEn = "Unexpected error: ${e.localizedMessage ?: "Please try again"}",
                cause = e
            )
        }
    }
}
