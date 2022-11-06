package com.vmobile.astronomypics.network

import com.vmobile.astronomypics.models.PlanetaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlanetaryNetworkService {

    @GET("planetary/apod")
    suspend fun getAstronomyPictureOfDay(
        @Query("api_key") apiKey: String
    ): Response<PlanetaryResponse>

    @GET("planetary/apod")
    suspend fun getAstronomyPictureOfDayByDate(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ): Response<PlanetaryResponse>
}