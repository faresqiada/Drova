package com.example.domain.model

/**
 * Evidence captured by the assigned captain before an order can move to PICKED_UP.
 * The model intentionally records only verifiable metadata; it does not claim that
 * OCR proves the spatial relationship between an order and an invoice.
 */
data class PickupProof(
    val proofId: String,
    val orderId: String,
    val captainId: String,
    val storagePath: String,
    val capturedAt: Long,
    val imageWidth: Int,
    val imageHeight: Int,
    val fileSizeBytes: Long,
    val contentType: String = "image/jpeg",
    val hasReceiptText: Boolean,
    val orderIdentifierMatched: Boolean,
    val validationStatus: PickupProofValidationStatus,
    val validationMessage: String = ""
)

enum class PickupProofValidationStatus {
    PENDING,
    VALIDATED,
    INVALID
}

enum class PickupProofFailure {
    CAPTAIN_NOT_ASSIGNED,
    ORDER_NOT_IN_ASSIGNED_STATE,
    INVALID_IMAGE,
    IMAGE_TOO_SMALL,
    IMAGE_TOO_LARGE,
    IMAGE_NOT_READABLE,
    RECEIPT_TEXT_NOT_FOUND,
    ORDER_IDENTIFIER_NOT_FOUND,
    DUPLICATE_CONFIRMATION,
    UPLOAD_FAILED,
    SECURITY_RULES_REJECTED,
    NETWORK_ERROR,
    UNKNOWN
}

sealed interface PickupProofConfirmation {
    data class Success(val proof: PickupProof) : PickupProofConfirmation
    data class Failure(
        val reason: PickupProofFailure,
        val messageAr: String,
        val messageEn: String = messageAr
    ) : PickupProofConfirmation
}
