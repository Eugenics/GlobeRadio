package com.eugenics.media_service.data.api

import com.eugenics.media_service.data.dto.StationRespondObject
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("stations/search")
    suspend fun searchByName(@Query("name") name: String): List<StationRespondObject>

    @POST("stations/search")
    suspend fun searchByTag(@Query("tag") tag: String): List<StationRespondObject>

    @POST("stations/search")
    suspend fun searchAll(
        @Query("codec") codec: String = "MP3",
        @Query("tag") tag: String = "relax"
    ): List<StationRespondObject>
}