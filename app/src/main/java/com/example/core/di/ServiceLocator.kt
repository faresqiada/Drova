package com.example.core.di

import com.example.core.network.ApiClient
import com.example.data.local.source.*
import com.example.data.remote.api.*
import com.example.data.remote.source.*
import com.example.data.repository.*
import com.example.domain.repository.*

/**
 * Dependency Injection Hub providing singleton lifecycle for Repositories,
 * Data Sources, Local Persistence, and Retrofit Networking services.
 */
object ServiceLocator {

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

    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(
            localDataSource = orderLocalDataSource,
            remoteDataSource = orderRemoteDataSource
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
            orderRepository = orderRepository
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
