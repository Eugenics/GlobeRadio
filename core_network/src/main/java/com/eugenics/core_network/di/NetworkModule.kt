package com.eugenics.core_network.di

import android.util.Log
import com.eugenics.core.interfaces.IStationsApi
import com.eugenics.core_network.constants.BASE_URL
import com.eugenics.core_network.network.api.ApiService
import com.eugenics.core_network.network.api.StationsApiImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(
        logger = { message ->
            Log.d(INTERCEPTOR_TAG, message)
        })
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                    .addHeader(HEADER_NAME, HEADER_VALUE)
                    .build()
            )
        }
        .build()

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
        .create(com.eugenics.core_network.network.api.ApiService::class.java)

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = okHttpClient

    @Singleton
    @Provides
    fun provideStationApi(): IStationsApi = StationsApiImpl(apiService = apiService)


    companion object {
        const val TAG = "NETWORK_MODULE"
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val CONTENT_TYPE_VALUE = "application/json"
        private const val INTERCEPTOR_TAG = "HTTP Interceptor"
        private const val HEADER_NAME = "ApplicationName"
        private const val HEADER_VALUE = "freeRadio"
    }
}