package com.example.core.network

import com.example.data.local.source.SessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Factory and provider for Retrofit and OkHttpClient instances.
 */
class ApiClient(
    private val sessionManager: SessionManager? = null,
    private val baseUrl: String = NetworkConfig.baseUrl
) {

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
            .header(NetworkConfig.HEADER_CONTENT_TYPE, "application/json")
            .header(NetworkConfig.HEADER_DEVICE_LOCALE, "ar-EG,ar;q=0.9,en;q=0.8")

        val token = sessionManager?.authToken?.value
        if (!token.isNullOrBlank()) {
            builder.header(NetworkConfig.HEADER_AUTHORIZATION, "Bearer $token")
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (com.example.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> create(): T {
        return createService(T::class.java)
    }
}
