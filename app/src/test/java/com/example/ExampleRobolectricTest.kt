package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.CaptainRepositoryImpl
import com.example.data.repository.OrderRepositoryImpl
import com.example.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DROVA", appName)
  }

  @Test
  fun `deterministic end-to-end order lifecycle scenario DROVA-1001`() = runBlocking {
    val orderRepository = OrderRepositoryImpl(pickupProofService = FakePickupProofService())
    val captainRepository = CaptainRepositoryImpl(orderRepository)

    // 1. Customer creates DROVA-1001 (2x Chicken Shawarma Meal, Cash on Delivery)
    val testOrder = Order(
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
          id = "item_shawarma_1",
          nameAr = "ساندوتش شاورما فراخ عربي عائلي",
          nameEn = "Arabic Chicken Shawarma Box",
          quantity = 2,
          unitPriceEgp = 145.0
        )
      ),
      subtotalEgp = 290.0,
      deliveryFeeEgp = 20.0,
      platformFeeEgp = 5.0,
      totalEgp = 315.0,
      status = OrderStatus.CREATED,
      paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
      createdAtFormatted = "اليوم، 02:00 م"
    )

    val createdId = orderRepository.createNewOrder(testOrder)
    assertEquals("DROVA-1001", createdId)

    val createdOrder = orderRepository.getOrderById("DROVA-1001")
    assertNotNull(createdOrder)
    assertEquals(OrderStatus.CREATED, createdOrder?.status)
    assertEquals(1, createdOrder?.timeline?.size)

    // 2. Restaurant sees DROVA-1001 and confirms (STAGE 2)
    val confirmSuccess = orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.RESTAURANT_CONFIRMED)
    assertTrue(confirmSuccess)
    assertEquals(OrderStatus.RESTAURANT_CONFIRMED, orderRepository.getOrderById("DROVA-1001")?.status)

    // 3. Restaurant starts cooking in kitchen (STAGE 3)
    val prepSuccess = orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.PREPARING)
    assertTrue(prepSuccess)
    assertEquals(OrderStatus.PREPARING, orderRepository.getOrderById("DROVA-1001")?.status)

    // 4. Restaurant finishes prep and marks ready for pickup (STAGE 4)
    val readySuccess = orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.READY_FOR_PICKUP)
    assertTrue(readySuccess)
    assertEquals(OrderStatus.READY_FOR_PICKUP, orderRepository.getOrderById("DROVA-1001")?.status)

    // Let coroutine flow emit to availableTasks
    delay(100)

    // 5. Captain sees incoming delivery in availableTasks & accepts (STAGE 5)
    val available = captainRepository.availableTasks.value
    assertTrue("Available tasks should contain DROVA-1001", available.any { it.orderId == "DROVA-1001" })

    val acceptSuccess = captainRepository.acceptTask("DROVA-1001")
    assertTrue(acceptSuccess)

    val orderAfterAccept = orderRepository.getOrderById("DROVA-1001")
    assertEquals(OrderStatus.CAPTAIN_ASSIGNED, orderAfterAccept?.status)
    assertNotNull(orderAfterAccept?.captainId)
    assertEquals("محمود عادل (كابتن DROVA)", orderAfterAccept?.captainName)
    assertEquals(OrderStatus.CAPTAIN_ASSIGNED, captainRepository.activeTask.value?.status)

    // 6. Captain arrives at restaurant; direct state update is rejected.
    val directPickupSuccess = captainRepository.updateTaskStatus("DROVA-1001", OrderStatus.PICKED_UP)
    assertFalse(directPickupSuccess)
    val proofFile = java.io.File.createTempFile("pickup-proof-robolectric", ".jpg")
    val pickupConfirmation = captainRepository.confirmPickup("DROVA-1001", proofFile)
    proofFile.delete()
    assertTrue(pickupConfirmation is PickupProofConfirmation.Success)
    assertEquals(OrderStatus.PICKED_UP, orderRepository.getOrderById("DROVA-1001")?.status)
    assertEquals(OrderStatus.PICKED_UP, captainRepository.activeTask.value?.status)

    // 7. Captain drives towards customer (STAGE 7)
    val onTheWaySuccess = captainRepository.updateTaskStatus("DROVA-1001", OrderStatus.ON_THE_WAY)
    assertTrue(onTheWaySuccess)
    assertEquals(OrderStatus.ON_THE_WAY, orderRepository.getOrderById("DROVA-1001")?.status)
    assertEquals(OrderStatus.ON_THE_WAY, captainRepository.activeTask.value?.status)

    // 8. Captain delivers order to customer (STAGE 8)
    val initialDeliveries = captainRepository.earnings.value.todayDeliveriesCount
    val deliverSuccess = captainRepository.updateTaskStatus("DROVA-1001", OrderStatus.DELIVERED)
    assertTrue(deliverSuccess)
    assertEquals(OrderStatus.DELIVERED, orderRepository.getOrderById("DROVA-1001")?.status)
    assertNull(captainRepository.activeTask.value)
    assertEquals(initialDeliveries + 1, captainRepository.earnings.value.todayDeliveriesCount)

    // 9. Customer confirms receipt -> COMPLETED (STAGE 9)
    val completeSuccess = orderRepository.advanceOrderStatus("DROVA-1001", OrderStatus.COMPLETED)
    assertTrue(completeSuccess)
    val finalOrder = orderRepository.getOrderById("DROVA-1001")
    assertEquals(OrderStatus.COMPLETED, finalOrder?.status)
    assertTrue(finalOrder?.isTerminal == true)
    assertEquals(9, finalOrder?.timeline?.size) // Initial CREATED + 8 stage transitions (9 events total)
  }

  @Test
  fun `invalid state transitions are strictly rejected globally`() = runBlocking {
    val orderRepository = OrderRepositoryImpl()

    val newOrder = Order(
      id = "test_invalid_flow",
      orderNumber = "DRV-9999",
      customerId = "cust_1",
      customerName = "Customer",
      customerPhone = "+201000000000",
      deliveryAddressAr = "Address",
      restaurantId = "rest_1",
      restaurantNameAr = "Restaurant",
      restaurantAddressAr = "Address",
      items = emptyList(),
      subtotalEgp = 100.0,
      deliveryFeeEgp = 20.0,
      status = OrderStatus.CREATED,
      createdAtFormatted = "الآن"
    )
    orderRepository.createNewOrder(newOrder)

    // CREATED -> PICKED_UP must fail
    assertFalse(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.PICKED_UP))

    // CREATED -> DELIVERED must fail
    assertFalse(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.DELIVERED))

    // Valid advance: CREATED -> RESTAURANT_CONFIRMED -> PREPARING
    assertTrue(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.RESTAURANT_CONFIRMED))
    assertTrue(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.PREPARING))

    // PREPARING -> ON_THE_WAY must fail
    assertFalse(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.ON_THE_WAY))

    // Valid advance: PREPARING -> READY_FOR_PICKUP
    assertTrue(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.READY_FOR_PICKUP))

    // READY_FOR_PICKUP -> DELIVERED must fail
    assertFalse(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.DELIVERED))

    // Valid advance: Assign Captain -> CAPTAIN_ASSIGNED
    assertTrue(orderRepository.assignCaptainToOrder("test_invalid_flow", "cap_1", "Mahmoud", "+201100000000"))

    // CAPTAIN_ASSIGNED -> COMPLETED must fail
    assertFalse(orderRepository.advanceOrderStatus("test_invalid_flow", OrderStatus.COMPLETED))
  }

  @Test
  fun `cancellation and rejection audit details preserved`() = runBlocking {
    val orderRepository = OrderRepositoryImpl()

    val cancelOrder = Order(
      id = "test_cancel_flow",
      orderNumber = "DRV-8888",
      customerId = "cust_1",
      customerName = "Customer",
      customerPhone = "+201000000000",
      deliveryAddressAr = "Address",
      restaurantId = "rest_1",
      restaurantNameAr = "Restaurant",
      restaurantAddressAr = "Address",
      items = emptyList(),
      subtotalEgp = 100.0,
      deliveryFeeEgp = 20.0,
      status = OrderStatus.CREATED,
      createdAtFormatted = "الآن"
    )
    orderRepository.createNewOrder(cancelOrder)

    val cancelReason = "العميل طلب تغيير العنوان"
    val cancelSuccess = orderRepository.cancelOrder("test_cancel_flow", cancelReason)
    assertTrue(cancelSuccess)

    val updatedCancel = orderRepository.getOrderById("test_cancel_flow")
    assertEquals(OrderStatus.CANCELLED, updatedCancel?.status)
    assertEquals(cancelReason, updatedCancel?.cancellationReason)

    val rejectOrder = Order(
      id = "test_reject_flow",
      orderNumber = "DRV-7777",
      customerId = "cust_1",
      customerName = "Customer",
      customerPhone = "+201000000000",
      deliveryAddressAr = "Address",
      restaurantId = "rest_1",
      restaurantNameAr = "Restaurant",
      restaurantAddressAr = "Address",
      items = emptyList(),
      subtotalEgp = 100.0,
      deliveryFeeEgp = 20.0,
      status = OrderStatus.CREATED,
      createdAtFormatted = "الآن"
    )
    orderRepository.createNewOrder(rejectOrder)

    val rejectReason = "نفاذ الكمية المطلوبة بالمطعم"
    val rejectSuccess = orderRepository.rejectOrder("test_reject_flow", rejectReason)
    assertTrue(rejectSuccess)

    val updatedReject = orderRepository.getOrderById("test_reject_flow")
    assertEquals(OrderStatus.REJECTED, updatedReject?.status)
    assertEquals(rejectReason, updatedReject?.rejectionReason)
  }
}
