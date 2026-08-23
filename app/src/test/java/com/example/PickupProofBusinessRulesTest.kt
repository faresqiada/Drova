package com.example

import com.example.data.repository.OrderRepositoryImpl
import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.PaymentMethod
import com.example.domain.model.PickupProof
import com.example.domain.model.PickupProofConfirmation
import com.example.domain.model.PickupProofValidationStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PickupProofBusinessRulesTest {
    @Test
    fun `direct captain assigned to picked up bypass is rejected`() = runBlocking {
        val repository = assignedRepository()
        assertFalse(repository.advanceOrderStatus("order-proof", OrderStatus.PICKED_UP))
    }

    @Test
    fun `invalid proof is rejected`() = runBlocking {
        val repository = assignedRepository()
        val result = repository.applyValidatedPickupProof("order-proof", "cap-1", proof(status = PickupProofValidationStatus.INVALID))
        assertTrue(result is com.example.core.result.DrovaResult.Error)
    }

    @Test
    fun `proof for another order is rejected`() = runBlocking {
        val repository = assignedRepository()
        val result = repository.applyValidatedPickupProof("order-proof", "cap-1", proof(orderId = "other-order"))
        assertTrue(result is com.example.core.result.DrovaResult.Error)
    }

    @Test
    fun `proof for another captain is rejected`() = runBlocking {
        val repository = assignedRepository()
        val result = repository.applyValidatedPickupProof("order-proof", "cap-1", proof(captainId = "cap-2"))
        assertTrue(result is com.example.core.result.DrovaResult.Error)
    }

    @Test
    fun `valid proof allows picked up`() = runBlocking {
        val repository = assignedRepository()
        val result = repository.applyValidatedPickupProof("order-proof", "cap-1", proof())
        assertTrue(result is com.example.core.result.DrovaResult.Success)
        assertEquals(OrderStatus.PICKED_UP, repository.getOrderById("order-proof")?.status)
        assertEquals("proof-1", repository.getOrderById("order-proof")?.pickupProof?.proofId)
    }

    @Test
    fun `duplicate confirmation is rejected and does not add another timeline event`() = runBlocking {
        val repository = assignedRepository()
        val first = proof()
        assertTrue(repository.applyValidatedPickupProof("order-proof", "cap-1", first) is com.example.core.result.DrovaResult.Success)
        val timelineSize = repository.getOrderById("order-proof")?.timeline?.size
        val second = repository.applyValidatedPickupProof("order-proof", "cap-1", first)
        assertTrue(second is com.example.core.result.DrovaResult.Error)
        assertEquals(timelineSize, repository.getOrderById("order-proof")?.timeline?.size)
    }

    @Test
    fun `unassigned captain cannot apply proof`() = runBlocking {
        val repository = assignedRepository()
        val result = repository.applyValidatedPickupProof("order-proof", "cap-other", proof(captainId = "cap-other"))
        assertTrue(result is com.example.core.result.DrovaResult.Error)
    }

    @Test
    fun `proof storage path keeps order and captain ownership segments`() {
        val proof = proof()
        assertTrue(proof.storagePath.contains("orders/order-proof/pickup-proof/cap-1/"))
        assertTrue(proof.storagePath.endsWith("proof-1.jpg"))
    }

    private fun assignedRepository(): OrderRepositoryImpl = OrderRepositoryImpl().also { repository ->
        runBlocking {
            repository.createNewOrder(sampleOrder())
            assertTrue(repository.assignCaptainToOrder("order-proof", "cap-1", "Captain", "+201000000000"))
        }
    }

    private fun sampleOrder() = Order(
        id = "order-proof",
        orderNumber = "DRV-PROOF-1",
        customerId = "cust-1",
        customerName = "Customer",
        customerPhone = "+201000000000",
        deliveryAddressAr = "Cairo",
        restaurantId = "rest-1",
        restaurantNameAr = "Restaurant",
        restaurantAddressAr = "Cairo",
        items = emptyList(),
        subtotalEgp = 100.0,
        deliveryFeeEgp = 20.0,
        status = OrderStatus.READY_FOR_PICKUP,
        paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
        createdAtFormatted = "الآن"
    )

    private fun proof(
        orderId: String = "order-proof",
        captainId: String = "cap-1",
        status: PickupProofValidationStatus = PickupProofValidationStatus.VALIDATED
    ) = PickupProof(
        proofId = "proof-1",
        orderId = orderId,
        captainId = captainId,
        storagePath = "orders/$orderId/pickup-proof/$captainId/proof-1.jpg",
        capturedAt = 1L,
        imageWidth = 1280,
        imageHeight = 960,
        fileSizeBytes = 100_000L,
        hasReceiptText = true,
        orderIdentifierMatched = true,
        validationStatus = status
    )
}
