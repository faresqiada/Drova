package com.example

import com.example.core.location.LocationUtils
import com.example.core.result.DrovaError
import com.example.core.result.DrovaResult
import com.example.data.local.source.SessionManager
import com.example.data.repository.*
import com.example.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProductionReadinessTest {

    @Test
    fun `test complete 9-stage canonical lifecycle with Ahmed Mostafa scenario DROVA-1001`() = runBlocking {
        val orderRepository = OrderRepositoryImpl()
        val captainRepository = CaptainRepositoryImpl(orderRepository)

        // 1. Customer Ahmed Mostafa creates order DROVA-1001 (2x Chicken Meal, Cash on Delivery)
        val order = Order(
            id = "DROVA-1001",
            orderNumber = "DRV-1001",
            customerId = "cust_ahmed",
            customerName = "Ahmed Mostafa",
            customerPhone = "+201012345678",
            deliveryAddressAr = "شارع النصر، المعادي، القاهرة",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما الريم المعادي",
            restaurantAddressAr = "شارع 9، المعادي، القاهرة",
            items = listOf(
                OrderItem(
                    id = "item_1",
                    nameAr = "وجبة شاورما دجاج عائلية",
                    nameEn = "Family Chicken Meal",
                    quantity = 2,
                    unitPriceEgp = 145.0
                )
            ),
            subtotalEgp = 290.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            discountEgp = 0.0,
            totalEgp = 315.0,
            status = OrderStatus.CREATED,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "اليوم، 02:00 م"
        )

        val createdId = orderRepository.createNewOrder(order)
        assertEquals("DROVA-1001", createdId)

        // Verify STAGE 1: CREATED
        var currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertNotNull(currentOrder)
        assertEquals(OrderStatus.CREATED, currentOrder?.status)
        assertEquals(1, currentOrder?.timeline?.size)

        // STAGE 2: Restaurant confirms
        assertTrue(orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.RESTAURANT_CONFIRMED))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.RESTAURANT_CONFIRMED, currentOrder?.status)

        // STAGE 3: Restaurant starts preparing
        assertTrue(orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.PREPARING))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.PREPARING, currentOrder?.status)

        // STAGE 4: Restaurant marks ready for pickup
        assertTrue(orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.READY_FOR_PICKUP))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.READY_FOR_PICKUP, currentOrder?.status)

        delay(80)

        // STAGE 5: Captain accepts
        val available = captainRepository.availableTasks.value
        assertTrue(available.any { it.orderId == "DROVA-1001" })
        assertTrue(captainRepository.acceptTask("DROVA-1001"))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.CAPTAIN_ASSIGNED, currentOrder?.status)
        assertNotNull(currentOrder?.captainId)

        // STAGE 6: Captain picks up from restaurant
        assertTrue(captainRepository.updateTaskStatus("DROVA-1001", OrderStatus.PICKED_UP))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.PICKED_UP, currentOrder?.status)

        // STAGE 7: Captain on the way to customer
        assertTrue(captainRepository.updateTaskStatus("DROVA-1001", OrderStatus.ON_THE_WAY))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.ON_THE_WAY, currentOrder?.status)

        // STAGE 8: Captain confirms delivery
        assertTrue(captainRepository.updateTaskStatus("DROVA-1001", OrderStatus.DELIVERED))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.DELIVERED, currentOrder?.status)

        // STAGE 9: Final closure & completion
        assertTrue(orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.COMPLETED))
        currentOrder = orderRepository.getOrderById("DROVA-1001")
        assertEquals(OrderStatus.COMPLETED, currentOrder?.status)
        assertTrue(currentOrder?.isTerminal == true)
        assertEquals(9, currentOrder?.timeline?.size)
    }

    @Test
    fun `test strict invalid state transition guards`() = runBlocking {
        val orderRepository = OrderRepositoryImpl()

        val sampleOrder = Order(
            id = "test_guard_order",
            orderNumber = "DRV-GUARD-1",
            customerId = "cust_guard",
            customerName = "Guard Tester",
            customerPhone = "+201099998888",
            deliveryAddressAr = "القاهرة",
            restaurantId = "rest_1",
            restaurantNameAr = "المطعم",
            restaurantAddressAr = "المعادي",
            items = emptyList(),
            subtotalEgp = 100.0,
            deliveryFeeEgp = 20.0,
            status = OrderStatus.CREATED,
            createdAtFormatted = "الآن"
        )
        orderRepository.createNewOrder(sampleOrder)

        // 1. CREATED -> PICKED_UP (Illegal bypass)
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.PICKED_UP))

        // 2. CREATED -> DELIVERED (Illegal bypass)
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.DELIVERED))

        // Valid advance: CREATED -> RESTAURANT_CONFIRMED -> PREPARING
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.RESTAURANT_CONFIRMED))
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.PREPARING))

        // 3. PREPARING -> ON_THE_WAY (Illegal bypass)
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.ON_THE_WAY))

        // Valid advance: PREPARING -> READY_FOR_PICKUP
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.READY_FOR_PICKUP))

        // 4. READY_FOR_PICKUP -> DELIVERED (Illegal bypass)
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.DELIVERED))

        // Valid advance: Assign Captain -> CAPTAIN_ASSIGNED
        assertTrue(orderRepository.assignCaptainToOrder("test_guard_order", "cap_test", "Captain Test", "+201100000000"))

        // 5. CAPTAIN_ASSIGNED -> COMPLETED (Illegal bypass)
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.COMPLETED))

        // Advance through proper stages: PICKED_UP -> ON_THE_WAY -> DELIVERED -> COMPLETED
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.PICKED_UP))
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.ON_THE_WAY))
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.DELIVERED))
        assertTrue(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.COMPLETED))

        // 6. COMPLETED -> PREPARING (Illegal backward transition)
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.PREPARING))
        assertFalse(orderRepository.advanceOrderStatus("test_guard_order", OrderStatus.CREATED))
    }

    @Test
    fun `test wallet and transaction idempotency and balance protection`() = runBlocking {
        val orderRepository = OrderRepositoryImpl()
        val captainRepository = CaptainRepositoryImpl(orderRepository)

        val initialBalance = captainRepository.earnings.value.walletBalanceEgp

        // Requesting negative amount must fail
        assertFalse(captainRepository.requestPayout(-50.0))

        // Requesting 0 amount must fail
        assertFalse(captainRepository.requestPayout(0.0))

        // Requesting amount greater than balance must fail
        assertFalse(captainRepository.requestPayout(initialBalance + 5000.0))

        // Requesting valid amount succeeds
        val validWithdrawal = 100.0
        assertTrue(captainRepository.requestPayout(validWithdrawal))
        assertEquals(initialBalance - validWithdrawal, captainRepository.earnings.value.walletBalanceEgp, 0.01)
    }

    @Test
    fun `test LocationUtils Haversine distance and ETA calculations`() {
        // Cairo Tower (30.0459, 31.2243) to Tahrir Square (30.0444, 31.2357) ~ 1.1 km
        val distanceKm = LocationUtils.calculateDistanceKm(30.0459, 31.2243, 30.0444, 31.2357)
        assertTrue(distanceKm in 0.8..1.5)

        val (arLabel, enLabel) = LocationUtils.formatDistance(distanceKm)
        assertTrue(arLabel.contains("كم"))
        assertTrue(enLabel.contains("km"))

        val travelMinutes = LocationUtils.estimateTravelTimeMinutes(distanceKm)
        assertTrue(travelMinutes >= 5)

        val totalMinutes = LocationUtils.estimateTotalDeliveryTimeMinutes(distanceKm)
        assertTrue(totalMinutes >= 15)
    }

    @Test
    fun `test SessionManager user session lifecycle`() {
        val session = SessionManager()
        assertFalse(session.isAuthenticated)
        assertNull(session.currentUser.value)

        val testUser = User(
            id = "usr_001",
            fullName = "Tariq Ibrahim",
            phone = "+201001112222",
            role = UserRole.RESTAURANT
        )

        session.setCurrentUser(testUser)
        session.setAuthToken("auth_header_token_xyz")
        assertTrue(session.isAuthenticated)
        assertEquals(testUser, session.currentUser.value)
        assertEquals("auth_header_token_xyz", session.authToken.value)

        session.clearSession()
        assertFalse(session.isAuthenticated)
        assertNull(session.currentUser.value)
        assertNull(session.authToken.value)
    }
}
