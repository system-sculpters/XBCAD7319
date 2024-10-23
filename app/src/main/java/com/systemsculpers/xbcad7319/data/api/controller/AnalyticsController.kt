package com.systemsculpers.xbcad7319.data.api.controller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systemsculpers.xbcad7319.data.api.retrofitclient.RetrofitClient
import com.systemsculpers.xbcad7319.data.api.service.AnalyticsService
import com.systemsculpers.xbcad7319.data.model.analytics.AnalyticsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnalyticsController : ViewModel() {
    // Retrofit API service instance for category-related network requests
    private var api: AnalyticsService = RetrofitClient.createService<AnalyticsService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val analyticsResponse: MutableLiveData<AnalyticsResponse> = MutableLiveData()

    // Fetches all categories associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `categoryList`, `status`, and `message` based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getAnalytics(userToken: String) {
        val token = "Bearer $userToken"
        val call = api.getAnalytics(token)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        //Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<AnalyticsResponse> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<AnalyticsResponse>, response: Response<AnalyticsResponse>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val analytics = response.body()
                    analytics?.let {
                        analyticsResponse.postValue(it)
                        status.postValue(true)
                        message.postValue("properties retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<AnalyticsResponse>, t: Throwable) {
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}