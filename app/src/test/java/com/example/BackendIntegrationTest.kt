package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.core.di.ServiceLocator
import com.google.firebase.FirebaseApp
import com.example.core.network.safeApiCall
import com.example.core.result.DrovaError
import com.example.core.result.DrovaResult
import com.example.data.local.source.SessionManager
import com.example.data.remote.dto.*
import com.example.data.repository.FinanceRepositoryImpl
import com.example.data.repository.OrderRepositoryImpl
import com.example.domain.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BackendIntegrationTest {

    @Before
    fun setUpServiceLocator() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        FirebaseApp.initializeApp(context)
        ServiceLocator.initialize(context)
    }

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `test OrderDto JSON serialization and deserialization`() {
        val adapter = moshi.adapter(OrderDto::class.java)

        val originalOrder = Order(
            id = "DROVA-2001",
            orderNumber = "DRV-2001",
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
                    nameAr = "ساندوتش شاورما",
                    nameEn = "Shawarma Sandwich",
                    quantity = 3,
                    unitPriceEgp = 95.0
                )
            ),
            subtotalEgp = 285.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            discountEgp = 0.0,
            totalEgp = 310.0,
            status = OrderStatus.CREATED,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "اليوم، 01:30 م"
        )

        val dto = originalOrder.toDto()
        val json = adapter.toJson(dto)
        assertNotNull(json)
        assertTrue(json.contains("DROVA-2001"))
        assertTrue(json.contains("DRV-2001"))

        val parsedDto = adapter.fromJson(json)
        assertNotNull(parsedDto)
        val domainFromDto = parsedDto!!.toDomain()

        assertEquals(originalOrder.id, domainFromDto.id)
        assertEquals(originalOrder.orderNumber, domainFromDto.orderNumber)
        assertEquals(originalOrder.customerId, domainFromDto.customerId)
        assertEquals(originalOrder.restaurantId, domainFromDto.restaurantId)
        assertEquals(originalOrder.totalEgp, domainFromDto.totalEgp, 0.01)
        assertEquals(originalOrder.status, domainFromDto.status)
        assertEquals(1, domainFromDto.items.size)
        assertEquals(3, domainFromDto.items[0].quantity)
    }

    @Test
    fun `test safeApiCall network error taxonomy mapping`() = runBlocking {
        // 1. Timeout Exception
        val timeoutResult: DrovaResult<String> = safeApiCall(Dispatchers.Unconfined) {
            throw SocketTimeoutException("Read timed out")
        }
        assertTrue(timeoutResult is DrovaResult.Error)
        val timeoutError = timeoutResult as DrovaResult.Error
        assertEquals(DrovaError.Network.Timeout, timeoutError.error)
        assertTrue(timeoutError.messageAr.contains("مهلة"))

        // 2. Unknown Host / No Internet Exception
        val noInternetResult: DrovaResult<String> = safeApiCall(Dispatchers.Unconfined) {
            throw UnknownHostException("api.drova.app")
        }
        assertTrue(noInternetResult is DrovaResult.Error)
        val noInternetError = noInternetResult as DrovaResult.Error
        assertEquals(DrovaError.Network.NoInternet, noInternetError.error)
        assertTrue(noInternetError.messageAr.contains("اتصال"))

        // 3. HTTP 401 Unauthorized
        val http401Result: DrovaResult<String> = safeApiCall(Dispatchers.Unconfined) {
            throw HttpException(Response.error<Any>(401, okhttp3.ResponseBody.create(null, "Unauthorized")))
        }
        assertTrue(http401Result is DrovaResult.Error)
        val http401Error = http401Result as DrovaResult.Error
        assertTrue(http401Error.error is DrovaError.Network.Unauthorized)

        // 4. HTTP 404 Not Found
        val http404Result: DrovaResult<String> = safeApiCall(Dispatchers.Unconfined) {
            throw HttpException(Response.error<Any>(404, okhttp3.ResponseBody.create(null, "Not Found")))
        }
        assertTrue(http404Result is DrovaResult.Error)
        val http404Error = http404Result as DrovaResult.Error
        assertTrue(http404Error.error is DrovaError.Network.NotFound)

        // 5. Successful call
        val successResult: DrovaResult<String> = safeApiCall(Dispatchers.Unconfined) {
            "DROVA_SUCCESS_DATA"
        }
        assertTrue(successResult is DrovaResult.Success)
        assertEquals("DROVA_SUCCESS_DATA", (successResult as DrovaResult.Success).data)
    }

    @Test
    fun `test SessionManager token and role management`() {
        val sessionManager = SessionManager()
        assertFalse(sessionManager.isAuthenticated)
        assertNull(sessionManager.authToken.value)

        sessionManager.setAuthToken("jwt_mock_token_123")
        assertEquals("jwt_mock_token_123", sessionManager.authToken.value)

        val testUser = User(
            id = "usr_test",
            fullName = "Tariq Ali",
            phone = "+201011112222",
            role = UserRole.CAPTAIN
        )
        sessionManager.setCurrentUser(testUser)
        assertTrue(sessionManager.isAuthenticated)
        assertEquals(testUser, sessionManager.currentUser.value)

        sessionManager.setSelectedRole(UserRole.CAPTAIN)
        assertEquals(UserRole.CAPTAIN, sessionManager.selectedRole.value)

        sessionManager.clearSession()
        assertFalse(sessionManager.isAuthenticated)
        assertNull(sessionManager.authToken.value)
        assertNull(sessionManager.currentUser.value)
    }

    @Test
    fun `test FinanceRepository revenue calculation`() = runBlocking {
        val orderRepo = OrderRepositoryImpl()
        val financeRepo = FinanceRepositoryImpl(
            orderRepository = orderRepo,
            restaurantRepository = ServiceLocator.restaurantRepository
        )

        // Create completed order for rest_1 with subtotal 200.0
        val completedOrder = Order(
            id = "finance_order_1",
            orderNumber = "DRV-FIN-1",
            customerId = "cust_1",
            customerName = "Customer",
            customerPhone = "+201000000000",
            deliveryAddressAr = "Address",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما الريم",
            restaurantAddressAr = "المعادي",
            items = listOf(
                OrderItem(
                    id = "item_1",
                    nameAr = "شاورما",
                    quantity = 2,
                    unitPriceEgp = 100.0
                )
            ),
            subtotalEgp = 200.0,
            deliveryFeeEgp = 20.0,
            status = OrderStatus.COMPLETED,
            createdAtFormatted = "الآن"
        )
        orderRepo.createNewOrder(completedOrder)

        val summaryResult = financeRepo.getRestaurantFinanceSummary("rest_1")
        assertTrue(summaryResult is DrovaResult.Success)
        val summary = (summaryResult as DrovaResult.Success).data
        assertTrue(summary.grossRevenueEgp >= 200.0)
        assertTrue(summary.netPayoutEgp > 0.0)
        assertTrue(summary.completedOrdersCount >= 1)
    }

    @Test
    fun `test ServiceLocator provides all required clean repository contracts`() {
        assertNotNull(ServiceLocator.authRepository)
        assertNotNull(ServiceLocator.userRepository)
        assertNotNull(ServiceLocator.orderRepository)
        assertNotNull(ServiceLocator.restaurantRepository)
        assertNotNull(ServiceLocator.menuRepository)
        assertNotNull(ServiceLocator.captainRepository)
        assertNotNull(ServiceLocator.walletRepository)
        assertNotNull(ServiceLocator.notificationRepository)
        assertNotNull(ServiceLocator.financeRepository)
    }
}
