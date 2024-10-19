package com.systemsculpers.xbcad7319.data.api.controller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.systemsculpers.xbcad7319.data.api.retrofitclient.RetrofitClient
import com.systemsculpers.xbcad7319.data.api.service.ChatService
import com.systemsculpers.xbcad7319.data.api.service.PropertyService
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.SendMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatController {
    // Retrofit API service instance for category-related network requests
    private var api: ChatService = RetrofitClient.createService<ChatService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val chatList: MutableLiveData<List<Chat>> = MutableLiveData()

    // Fetches all categories associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `categoryList`, `status`, and `message` based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getChats(userToken: String, id: String) {
        val token = "Bearer $userToken"
        val call = api.getChats(token, id)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<List<Chat>> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val categories = response.body()
                    categories?.let {
                        chatList.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    // Handle unsuccessful responses, e.g., a 4xx or 5xx status code
                    chatList.postValue(listOf())
                    //Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
                chatList.postValue(listOf())
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
    fun sendMessage(userToken: String, sendMessage: SendMessage) {
        val token = "Bearer $userToken"
        api.sendMessage(token, sendMessage).enqueue(object : Callback<SendMessage> {
            override fun onResponse(call: Call<SendMessage>, response: Response<SendMessage>) {
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
            override fun onFailure(call: Call<SendMessage>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}