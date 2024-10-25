package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.Property
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PropertyService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("property")
    fun getProperties(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): Call<List<Property>>


    // Creates a new category based on the provided category details.
    // This function sends a POST request to the "category/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Category object as the request body and returns a Call object containing the created Category.
    @Multipart
    @POST("property/create")
    fun createProperty(
        @Header("Authorization") token: String,
        @Part("property") property: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Call<Property>

    // Updates an existing category identified by its ID.
    // This function sends a PUT request to the "category/{id}" endpoint.
    // It requires an authorization token in the header, the category ID in the path, and a Category object in the request body.
    // Returns a Call object containing the updated Category.
    @Multipart
    @PUT("property/{id}")
    fun updateProperty(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part("property") property: RequestBody,
        @Part images: List<MultipartBody.Part?>?): Call<Property>

    // Deletes a category identified by its ID.
    // This function sends a DELETE request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the category ID in the path.
    // Returns a Call object with no content on successful deletion.
    @DELETE("property/{id}")
    fun deleteProperty(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>

}