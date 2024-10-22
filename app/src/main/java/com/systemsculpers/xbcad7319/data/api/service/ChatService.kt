package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.Message
import com.systemsculpers.xbcad7319.data.model.MessageResponse
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.SendMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatService {

    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("chat/{id}")
    fun getChats(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Chat>>

    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("chat/{id}")
    fun getMessages(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Message>>

    // Creates a new category based on the provided category details.
    // This function sends a POST request to the "category/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Category object as the request body and returns a Call object containing the created Category.
    @POST("chat/send")
    fun sendMessage(@Header("Authorization") token: String, @Body sendMessage: SendMessage): Call<MessageResponse>

    // Creates a new category based on the provided category details.
    // This function sends a POST request to the "category/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Category object as the request body and returns a Call object containing the created Category.
    @POST("chat/send-new-message")
    fun sendNewMessage(@Header("Authorization") token: String, @Body sendMessage: SendMessage): Call<MessageResponse>

}