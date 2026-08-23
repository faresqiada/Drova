package com.example.data.pickupproof

import com.example.domain.model.Order
import com.example.domain.model.PickupProofConfirmation
import java.io.File

interface PickupProofService {
    suspend fun confirmPickup(order: Order, captainId: String, imageFile: File): PickupProofConfirmation
}
