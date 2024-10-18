package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.LocationResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("search")
    fun searchLocations(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1
    ): Call<List<LocationResult>>
}