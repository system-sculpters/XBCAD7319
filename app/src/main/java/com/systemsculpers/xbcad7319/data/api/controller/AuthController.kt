package com.systemsculpers.xbcad7319.data.api.controller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systemsculpers.xbcad7319.data.api.retrofitclient.RetrofitClient
import com.systemsculpers.xbcad7319.data.api.service.AuthService
import com.systemsculpers.xbcad7319.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthController: ViewModel(){ // api: Retrofit client for making API requests related to authentication.
    var api: AuthService = RetrofitClient.createService<AuthService>()

    // status: LiveData that holds the status of API requests (true for success, false for failure).
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // message: LiveData that contains success or error messages based on API responses.
    val message: MutableLiveData<String> = MutableLiveData()

    // userData: LiveData holding the user information retrieved during registration or login.
    val userData: MutableLiveData<User> = MutableLiveData()

    // Function to handle user registration.
    // Takes a User object, makes a registration API call, and updates LiveData based on the response.

        // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun register(user: User) {
        api.register(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If registration is successful, retrieve the created user and update LiveData.
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User registered successfully")  // Post success message.
                        userData.postValue(it)  // Post the created user data.
                        Log.d("MainActivity", "User created: $it")
                    }
                } else {
                    // Handle failure when the response is not successful (e.g., non-2xx status code).
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }

    // Function to handle user login.
    // Takes a User object, makes a login API call, and updates LiveData based on the response.

        // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun login(user: User) {
        api.login(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If login is successful, retrieve the logged-in user and update LiveData.
                    val loggedInUser = response.body()
                    loggedInUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User logged in successfully")  // Post success message.
                        userData.postValue(it)  // Post the logged-in user data.
                        Log.d("MainActivity", "User logged in: $it")
                    }
                } else {
                    // Handle failure when the response is not successful.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }
}