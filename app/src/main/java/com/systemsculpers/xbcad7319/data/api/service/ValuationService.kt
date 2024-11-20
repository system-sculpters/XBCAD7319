package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.Valuation
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ValuationService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("valuation/agent/{id}")
    fun getValuations(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Valuation>>


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("valuation/user/{id}")
    fun getUserValuations(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Valuation>>

    // Creates a new category based on the provided category details.
    // This function sends a POST request to the "category/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Category object as the request body and returns a Call object containing the created Category.
    @POST("valuation/create")
    fun createValuation(@Header("Authorization") token: String, @Body valuation: Valuation): Call<Valuation>

    // Updates an existing category identified by its ID.
    // This function sends a PUT request to the "category/{id}" endpoint.
    // It requires an authorization token in the header, the category ID in the path, and a Category object in the request body.
    // Returns a Call object containing the updated Category.
    @PUT("valuation/{id}/status")
    fun updateValuation(@Header("Authorization") token: String, @Path("id") id: String, @Body valuation: Valuation): Call<Valuation>


}