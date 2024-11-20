package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    // Route to get all users
    @GET("user/get-all-users")
    fun getAllUsers(@Header("Authorization") token: String): Call<List<User>>

    // Route to get users with a specific role (e.g., 'user')
    @GET("user/get-users-by-role")
    fun getUsersByRole(@Header("Authorization") token: String): Call<List<User>>

    // Route to update user role (e.g., from user to agent)
    @PUT("user/update-user-role/{id}")
    fun updateUserRole(@Header("Authorization") token: String, @Path("id") id: String, @Body user: User): Call<Void>

    // Route to update user details (e.g., fullName, email, phoneNumber)
    @PUT("user/update-user-details/{id}")
    fun updateUserDetails(@Header("Authorization") token: String, @Path("id") id: String, @Body user: User): Call<Void>

    // Route to update user details (e.g., fullName, email, phoneNumber)
    @DELETE("user/delete-user/{id}")
    fun deleteUser(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>


    // Route to update password
    @PUT("user/update-password/{id}")
    fun updatePassword(@Header("Authorization") token: String, @Path("id") id: String, @Body user: User): Call<Void>

}