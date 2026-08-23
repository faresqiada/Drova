package com.example.core.di

import android.content.Context
import com.example.BuildConfig
import com.example.core.network.ApiClient
import com.example.data.local.source.*
import com.example.data.remote.api.*
import com.example.data.remote.source.*
import com.example.data.pickupproof.FirebasePickupProofService
import com.example.data.repository.*
import com.example.domain.repository.*

/**
 * Dependency Injection Hub providing singleton lifecycle for Repositories,
 * Data Sources, Local Persistence, and Retrofit Networking services.
 */
object ServiceLocator {

    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    private fun requireApplicationContext(): Context = checkNotNull(applicationContext) {
        "ServiceLocator.initialize(context) must be called from Application/Activity before Firebase proof services are used."
    }

    // ==========================================
    // Session & Network Infrastructure
    // ==========================================
    val sessionManager: SessionManager by lazy { SessionManager() }

    val apiClient: ApiClient by lazy { ApiClient(sessionManager) }

    // Retrofit API Services
    val authApiService: AuthApiService by lazy { apiClient.create<AuthApiService>() }
    val orderApiService: OrderApiService by lazy { apiClient.create<OrderApiService>() }
    val restaurantApiService: RestaurantApiService by lazy { apiClient.create<RestaurantApiService>() }
    val captainApiService: CaptainApiService by lazy { apiClient.create<CaptainApiService>() }
    val walletApiService: WalletApiService by lazy { apiClient.create<WalletApiService>() }
    val notificationApiService: NotificationApiService by lazy { apiClient.create<NotificationApiService>() }

    // ==========================================
    // Data Sources
    // ==========================================
    // Local Data Sources
    val orderLocalDataSource: OrderLocalDataSource by lazy { OrderLocalDataSourceImpl() }
    val restaurantLocalDataSource: RestaurantLocalDataSource by lazy { RestaurantLocalDataSourceImpl() }

    // Remote Data Sources
    val authRemoteDataSource: AuthRemoteDataSource by lazy { AuthRemoteDataSourceImpl(authApiService) }
    val orderRemoteDataSource: OrderRemoteDataSource by lazy { OrderRemoteDataSourceImpl(orderApiService) }
    val restaurantRemoteDataSource: RestaurantRemoteDataSource by lazy { RestaurantRemoteDataSourceImpl(restaurantApiService) }
    val captainRemoteDataSource: CaptainRemoteDataSource by lazy { CaptainRemoteDataSourceImpl(captainApiService) }
    val walletRemoteDataSource: WalletRemoteDataSource by lazy { WalletRemoteDataSourceImpl(walletApiService) }
    val notificationRemoteDataSource: NotificationRemoteDataSource by lazy { NotificationRemoteDataSourceImpl(notificationApiService) }

    // ==========================================
    // Domain Repositories
    // ==========================================
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            sessionManager = sessionManager,
            remoteDataSource = authRemoteDataSource
        )
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            sessionManager = sessionManager,
            remoteDataSource = authRemoteDataSource
        )
    }

    val pickupProofService: FirebasePickupProofService by lazy {
        FirebasePickupProofService(requireApplicationContext())
    }

    val adminRepository: AdminRepository by lazy {
        AdminRepositoryImpl()
    }

    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(
            localDataSource = orderLocalDataSource,
            remoteDataSource = orderRemoteDataSource,
            pickupProofService = pickupProofService
        )
    }

    val restaurantRepository: RestaurantRepository by lazy {
        RestaurantRepositoryImpl(
            localDataSource = restaurantLocalDataSource,
            remoteDataSource = restaurantRemoteDataSource
        )
    }

    val menuRepository: MenuRepository by lazy {
        MenuRepositoryImpl(restaurantRepository = restaurantRepository)
    }

    val captainRepository: CaptainRepository by lazy {
        CaptainRepositoryImpl(
            orderRepository = orderRepository,
            captainIdProvider = {
                sessionManager.firebaseUid.value
                    ?: sessionManager.currentUser.value?.id
                    ?: if (BuildConfig.DEBUG) "cap_1" else ""
            }
        )
    }

    val walletRepository: WalletRepository by lazy {
        WalletRepositoryImpl(
            captainRepository = captainRepository,
            remoteDataSource = walletRemoteDataSource
        )
    }

    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(
            captainRepository = captainRepository,
            remoteDataSource = notificationRemoteDataSource
        )
    }

    val financeRepository: FinanceRepository by lazy {
        FinanceRepositoryImpl(
            orderRepository = orderRepository,
            restaurantRepository = restaurantRepository
        )
    }
}
