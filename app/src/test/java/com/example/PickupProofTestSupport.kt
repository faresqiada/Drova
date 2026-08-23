package com.example

import com.example.data.pickupproof.PickupProofService
import com.example.domain.model.Order
import com.example.domain.model.PickupProof
import com.example.domain.model.PickupProofConfirmation
import com.example.domain.model.PickupProofValidationStatus
import java.io.File

class FakePickupProofService : PickupProofService {
    override suspend fun confirmPickup(
        order: Order,
        captainId: String,
        imageFile: File
    ): PickupProofConfirmation {
        return PickupProofConfirmation.Success(
            PickupProof(
                proofId = "proof-${order.id}",
                orderId = order.id,
                captainId = captainId,
                storagePath = "orders/${order.id}/pickup-proof/$captainId/proof-${order.id}.jpg",
                capturedAt = 1L,
                imageWidth = 1280,
                imageHeight = 960,
                fileSizeBytes = 100_000L,
                hasReceiptText = true,
                orderIdentifierMatched = true,
                validationStatus = PickupProofValidationStatus.VALIDATED,
                validationMessage = "test"
            )
        )
    }
}
