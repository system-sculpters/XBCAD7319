package com.systemsculpers.xbcad7319.data.api.controller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systemsculpers.xbcad7319.data.api.retrofitclient.RetrofitClient
import com.systemsculpers.xbcad7319.data.api.service.BookmarkService
import com.systemsculpers.xbcad7319.data.api.service.UserService
import com.systemsculpers.xbcad7319.data.model.Bookmark
import com.systemsculpers.xbcad7319.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookmarkController: ViewModel() {
    // Retrofit API service instance for category-related network requests
    private var api: BookmarkService = RetrofitClient.createService<BookmarkService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()


    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun bookmarkProperty(userToken: String, propertyId: String, bookmark: Bookmark) {
        val token = "Bearer $userToken"
        val call = api.bookmarkProperty(token, propertyId, bookmark)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<Void> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    status.postValue(true)
                    message.postValue("Categories retrieved")

                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<Void>, t: Throwable) {
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun unBookmarkProperty(userToken: String, propertyId: String, userId: String) {
        val token = "Bearer $userToken"
        val call = api.unBookmarkProperty(token, propertyId, userId)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<Void> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    status.postValue(true)
                    message.postValue("Categories retrieved")

                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<Void>, t: Throwable) {
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}