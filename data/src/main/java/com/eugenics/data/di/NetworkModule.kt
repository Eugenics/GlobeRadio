package com.eugenics.data.di

import android.util.Log
import com.eugenics.data.data.api.ApiService
import com.eugenics.data.data.datasources.FakeNetworkDataSourceImpl
import com.eugenics.data.data.datasources.INetworkDataSourceImpl
import com.eugenics.data.interfaces.repository.INetworkDataSource
import com.eugenics.media_service.data.constants.BASE_URL
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
import javax.inject.Named
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

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                    .addHeader("ApplicationName", "freeRadio")
                    .build()
            )
        }
        .build()

    private val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideApi(): ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)

    @Provides
    @Singleton
    @Named(NETWORK_DATA_SOURCE_NAME)
    fun provideNetworkDataSource(api: ApiService): INetworkDataSource =
        INetworkDataSourceImpl(apiService = api)

    @Provides
    @Singleton
    @Named(FAKE_DATA_SOURCE_NAME)
    fun provideFakeDataSource(): INetworkDataSource =
        FakeNetworkDataSourceImpl()

    companion object {
        const val FAKE_DATA_SOURCE_NAME = "fakeDataSource"
        const val NETWORK_DATA_SOURCE_NAME = "networkDataSource"
        private const val CONTENT_TYPE_HEADER = "Content-Type"
        private const val CONTENT_TYPE_VALUE = "application/json"
        private const val INTERCEPTOR_TAG = "HTTP Interceptor"
    }
}