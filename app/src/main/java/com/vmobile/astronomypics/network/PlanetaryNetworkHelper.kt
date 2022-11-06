package com.vmobile.astronomypics.network

import com.vmobile.astronomypics.utils.GenericConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlanetaryNetworkHelper {
    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GenericConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}