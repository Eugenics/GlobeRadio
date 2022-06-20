package com.eugenics.freeradio.data.network

import com.eugenics.freeradio.data.dto.SearchStationRespond
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("stations/search")
    suspend fun searchByName(@Query("name") name: String): List<SearchStationRespond>

    @GET("stations/search")
    suspend fun searchByTag(@Query("tag") tag: String): List<SearchStationRespond>

    @GET("stations")
    suspend fun searchAll(): List<SearchStationRespond>
}