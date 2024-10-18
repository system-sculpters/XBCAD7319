package com.systemsculpers.xbcad7319.data.api.retrofitclient

import com.systemsculpers.xbcad7319.data.api.service.MapService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapRetrofitClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MapService by lazy {
        retrofit.create(MapService::class.java)
    }
}