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
    val categoryList: MutableLiveData<List<LocationResult>> = MutableLiveData()

    fun fetchSuggestions(query: String) {
        MapRetrofitClient.api.searchLocations(query).enqueue(object : retrofit2.Callback<List<LocationResult>> {
            override fun onResponse(call: Call<List<LocationResult>>, response: retrofit2.Response<List<LocationResult>>) {
                if (response.isSuccessful) {
                    val locationResults = response.body()
                    if (!locationResults.isNullOrEmpty()) {
                        val firstResult = locationResults[0]
                        val address = firstResult.address
                        //categoryList.postValue(locationResults)
                        address?.let {
                            val houseNumber = it.house_number ?: "No house number"
                            val street = it.road ?: "Unknown street"
                            val city = it.city ?: "Unknown city"
                            val addressText = "$houseNumber $street, $city"

                            status.postValue(true)
                            message.postValue("Categories retrieved")
                            Log.d("suggestions", "suggestions: ${addressText} \nlat ${firstResult.lat}\nlong ${firstResult.lon}")
                            // Use this data in your UI (e.g., show it in a TextView or Marker)

                        }
                    }
                }
                if (response.isSuccessful) {
                    response.body()?.let { results ->
                        val suggestions = results.map { it.display_name }

                    }
                }else {
                    // Handle the failure of the category deletion request
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LocationResult>>, t: Throwable) {
                // Handle error
            }
        })
    }
}