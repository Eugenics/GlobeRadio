package com.eugenics.core_network.network.api

import com.eugenics.core_network.dto.ApiRequest
import com.eugenics.core_network.dto.StationRespondObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("stations/search")
    suspend fun searchByName(@Query("name") name: String): List<StationRespondObject>

    @POST("stations/search")
    suspend fun searchByTag(@Query("tag") tag: String): List<StationRespondObject>

    @POST("stations/search")
    suspend fun searchAll(@Body body: ApiRequest = ApiRequest()): List<StationRespondObject>
}