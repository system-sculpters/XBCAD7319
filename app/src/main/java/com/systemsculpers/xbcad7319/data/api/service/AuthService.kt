package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Registers a new user with the provided user details.
    // It sends a POST request to the "auth/signup" endpoint.
    // The function returns a Call object containing the User data of the newly registered user.
    @POST("auth/signup")
    fun register(@Body user: User): Call<User>

    // Authenticates a user using their login credentials.
    // This function sends a POST request to the "auth/signin" endpoint.
    // Upon success, it returns a Call object containing the User data for the logged-in user.
    @POST("auth/signin")
    fun login(@Body user: User): Call<User>

}