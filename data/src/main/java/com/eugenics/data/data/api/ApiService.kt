package com.eugenics.data.data.api

import com.eugenics.data.data.dto.ApiRequest
import com.eugenics.data.data.dto.StationRespondObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("stations/search")
    suspend fun searchByName(@Query("name") name: String): List<StationRespondObject>

    @POST("stations/search")
    suspend fun searchByTag(@Query("tag") tag: String): List<StationRespondObject>

    @POST("stations/search")
    suspend fun searchAll(
        @Body body: ApiRequest = ApiRequest()
//        @Query("codec") codec: String = "AAC%2B",
//        @Query("tag") tag: String = "relax"
    ): List<StationRespondObject>
}