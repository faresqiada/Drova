package com.example

import com.example.domain.model.DeliveryTask
import com.example.presentation.captain.CaptainOrderOfferPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CaptainOrderOfferPolicyTest {
    @Test
    fun `pending offer is surfaced without replacing active task`() {
        val active = task("active")
        val nearby = task("nearby")

        val offer = CaptainOrderOfferPolicy.findOrderInYourWay(active, listOf(active, nearby))

        assertEquals("nearby", offer?.orderId)
        assertEquals("active", active.orderId)
    }

    @Test
    fun `no active task means no order in your way notification`() {
        assertNull(CaptainOrderOfferPolicy.findOrderInYourWay(null, listOf(task("nearby"))))
    }

    private fun task(id: String) = DeliveryTask(
        orderId = id,
        orderNumber = "DRV-$id",
        restaurantNameAr = "مطعم",
        restaurantAddressAr = "عنوان المطعم",
        customerName = "عميل",
        customerAddressAr = "عنوان العميل",
        pickupDistanceKm = 1.0,
        dropoffDistanceKm = 1.0,
        itemsSummary = "طلب",
        estimatedEarningsEgp = 25.0,
        estimatedTimeMin = 10
    )
}
