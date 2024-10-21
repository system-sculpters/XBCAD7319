package com.systemsculpers.xbcad7319.data.api.controller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systemsculpers.xbcad7319.data.api.retrofitclient.MapRetrofitClient
import com.systemsculpers.xbcad7319.data.model.LocationResult
import retrofit2.Call

class MapController: ViewModel() {
    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val locationList: MutableLiveData<List<LocationResult>?> = MutableLiveData()

    fun fetchSuggestions(query: String) {
        MapRetrofitClient.api.searchLocations(query).enqueue(object : retrofit2.Callback<List<LocationResult>> {
            override fun onResponse(call: Call<List<LocationResult>>, response: retrofit2.Response<List<LocationResult>>) {
                if (response.isSuccessful) {
                    val locationResults = response.body()
                    if (!locationResults.isNullOrEmpty()) {
                        // Post the list of locations to the LiveData
                        locationList.postValue(locationResults)

                        // Notify UI of success
                        status.postValue(true)
                        message.postValue("Suggestions retrieved successfully")
                    } else {
                        // Handle empty results
                        locationList.postValue(null)
                        status.postValue(false)
                        message.postValue("No suggestions found")
                    }
                } else {
                    // Handle non-successful responses
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LocationResult>>, t: Throwable) {
                // Handle failure due to network issues or other errors
                status.postValue(false)
                message.postValue("Request failed: ${t.localizedMessage}")
            }
        })
    }
}