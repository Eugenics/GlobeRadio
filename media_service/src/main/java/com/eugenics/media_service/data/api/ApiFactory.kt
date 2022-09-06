package com.eugenics.media_service.data.api

import android.util.Log
import com.eugenics.media_service.data.constants.BASE_URL
import com.eugenics.media_service.domain.interfaces.IFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory : IFactory<ApiService> {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor(
        logger = { message ->
            Log.d(INTERCEPTOR_TAG, message)
        })
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
//        .addInterceptor { chain ->
//            chain.proceed(
//                Request.Builder()
//                    .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
//                    .build()
//            )
//        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    override fun create(): ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)

    private const val CONTENT_TYPE_HEADER = "Content-Type"
    private const val CONTENT_TYPE_VALUE = "application/json"
    private const val INTERCEPTOR_TAG = "HTTP Interceptor"
}