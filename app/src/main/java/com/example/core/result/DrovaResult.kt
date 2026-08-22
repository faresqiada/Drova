package com.example.core.result

/**
 * Standard domain and UI result wrapper for all DROVA operations.
 */
sealed class DrovaResult<out T> {
    data class Success<out T>(val data: T) : DrovaResult<T>()
    data class Error(
        val error: DrovaError,
        val messageAr: String,
        val messageEn: String,
        val cause: Throwable? = null
    ) : DrovaResult<Nothing>()
    object Loading : DrovaResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    inline fun onSuccess(action: (T) -> Unit): DrovaResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Error) -> Unit): DrovaResult<T> {
        if (this is Error) action(this)
        return this
    }

    inline fun <R> map(transform: (T) -> R): DrovaResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }
}

/**
 * Structured taxonomy of errors across network, business logic, auth, and storage.
 */
sealed interface DrovaError {
    sealed interface Network : DrovaError {
        object NoInternet : Network
        object Timeout : Network
        data class Unauthorized(val code: Int = 401) : Network
        data class Forbidden(val code: Int = 403) : Network
        data class NotFound(val code: Int = 404) : Network
        data class ValidationError(val code: Int = 422, val details: Map<String, String> = emptyMap()) : Network
        data class ServerError(val code: Int = 500) : Network
        data class Unknown(val message: String? = null) : Network
    }

    sealed interface Domain : DrovaError {
        data class InvalidStateTransition(val from: String, val to: String) : Domain
        data class OrderNotFound(val orderId: String) : Domain
        data class UnauthorizedRole(val required: String, val actual: String) : Domain
        object InsufficientBalance : Domain
        object ActiveDeliveryInProgress : Domain
        object InvalidCredentials : Domain
        data class ValidationFailed(val field: String, val reason: String) : Domain
    }

    sealed interface Storage : DrovaError {
        object ReadError : Storage
        object WriteError : Storage
        object SessionExpired : Storage
    }
}
