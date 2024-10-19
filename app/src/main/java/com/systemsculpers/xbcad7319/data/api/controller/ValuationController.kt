package com.systemsculpers.xbcad7319.data.api.controller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.systemsculpers.xbcad7319.data.api.retrofitclient.RetrofitClient
import com.systemsculpers.xbcad7319.data.api.service.ValuationService
import com.systemsculpers.xbcad7319.data.model.Valuation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValuationController: ViewModel() {
    // Retrofit API service instance for category-related network requests
    private var api: ValuationService = RetrofitClient.createService<ValuationService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val valuationList: MutableLiveData<List<Valuation>> = MutableLiveData()

    // Fetches all categories associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `categoryList`, `status`, and `message` based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getValuations(userToken: String, id: String) {
        val token = "Bearer $userToken"
        val call = api.getValuations(token, id)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<List<Valuation>> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<List<Valuation>>, response: Response<List<Valuation>>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val categories = response.body()
                    categories?.let {
                        valuationList.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    // Handle unsuccessful responses, e.g., a 4xx or 5xx status code
                    valuationList.postValue(listOf())
                    //Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<List<Valuation>>, t: Throwable) {
                valuationList.postValue(listOf())
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to create a new category for the user.
    // Takes a user token for authentication and a Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun createValuation(userToken: String, valuation: Valuation) {
        val token = "Bearer $userToken"
        api.createValuation(token, valuation).enqueue(object : Callback<Valuation> {
            override fun onResponse(call: Call<Valuation>, response: Response<Valuation>) {
                if (response.isSuccessful) {
                    // On successful category creation, update the status and message
                    val createdCategory = response.body()
                    createdCategory?.let {
                        status.postValue(true)
                        message.postValue("Category created: $it")
                        Log.d("MainActivity", "Category created: $it")
                    }
                } else {
                    // Handle the failure of the category creation
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handles network or other request failures
            override fun onFailure(call: Call<Valuation>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to update an existing category identified by `id`.
    // Takes a user token, category ID, and the updated Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun updateValuation(userToken: String, id: String, valuation: Valuation) {
        val token = "Bearer $userToken"
        api.updateValuation(token, id, valuation).enqueue(object : Callback<Valuation> {
            override fun onResponse(call: Call<Valuation>, response: Response<Valuation>) {
                if (response.isSuccessful) {
                    // On successful category update, update the status and message
                    val updatedCategory = response.body()
                    updatedCategory?.let {
                        status.postValue(true)
                        message.postValue("Category updated: $it")
                        Log.d("MainActivity", "Category updated: $it")
                    }
                } else {
                    // Handle the failure of the category update request
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handles network or other request failures
            override fun onFailure(call: Call<Valuation>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to delete a category identified by `id`.
    // Takes a user token and category ID.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getUserValuations(userToken: String, id: String) {
        val token = "Bearer $userToken"
        val call = api.getUserValuations(token, id)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<List<Valuation>> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<List<Valuation>>, response: Response<List<Valuation>>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val categories = response.body()
                    categories?.let {
                        valuationList.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    // Handle unsuccessful responses, e.g., a 4xx or 5xx status code
                    valuationList.postValue(listOf())
                    //Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<List<Valuation>>, t: Throwable) {
                valuationList.postValue(listOf())
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}