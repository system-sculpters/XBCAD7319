package com.systemsculpers.xbcad7319.view.fragment

import android.app.AlertDialog
import com.systemsculpers.xbcad7319.R

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.systemsculpers.xbcad7319.data.api.controller.ChatController
import com.systemsculpers.xbcad7319.data.model.Message
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.data.model.SendMessage
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyDetailsBinding
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyListingsBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyDetailsAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyDetailsImageAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeFilterAdapter
import com.systemsculpers.xbcad7319.view.fragment.MessagesFragment.Companion.CHAT_ID_ARG
import com.systemsculpers.xbcad7319.view.fragment.MessagesFragment.Companion.MESSAGES_ARG

class PropertyDetails : Fragment() {
//    val propertyTypes: MutableList<PropertyType> = mutableListOf(
//        PropertyType(, R.drawable.baseline_home_filled_24),
//        PropertyType(context.getString(R.string.rental), R.drawable.rental_icon),
//        PropertyType(context.getString(R.string.land), R.drawable.land_icon)
//    )

    // View binding object for accessing views in the layout
    private var _binding: FragmentPropertyDetailsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private var property: Property? = Property()

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyDetailsAdapter: PropertyDetailsAdapter

    private lateinit var imageSlideAdapter: PropertyDetailsImageAdapter

    private lateinit var chatController: ChatController

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            property = it.getParcelable(PROPERTY_DETAILS_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)

        propertyDetailsAdapter = PropertyDetailsAdapter()

        chatController = ViewModelProvider(this).get(ChatController::class.java)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        setupPropertyDetails()
        setPropertyTypes()
        // Find the message button (LinearLayout in your case)
        binding.contactAgent.setOnClickListener {
            showMessageDialog()
        }

        binding.seeOnMaps.setOnClickListener{

        }

        return binding.root
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 4) // 3 icons per row

        // Create an empty list for property details
        val propertyDetails: MutableList<PropertyType> = mutableListOf()

        // Check if the property type is "land"
        if (property?.propertyType == "land") {
            // Only add size detail for land
            propertyDetails.add(PropertyType("${property?.size} ${getString(R.string.sqrf)}", R.drawable.baseline_square_foot_24))
        } else {
            // Add all property details (rooms, bathrooms, parking, size) if it's not "land"
            propertyDetails.add(PropertyType("${property?.rooms} ${getString(R.string.bed)}", R.drawable.baseline_bed_24))
            propertyDetails.add(PropertyType("${property?.bathrooms} ${getString(R.string.bath)}", R.drawable.baseline_bathtub_24))
            propertyDetails.add(PropertyType("${property?.parking} ${getString(R.string.parking)}", R.drawable.baseline_car_24))
            propertyDetails.add(PropertyType("${property?.size} ${getString(R.string.sqrf)}", R.drawable.baseline_square_foot_24))
        }

        // Update the adapter with the property details
        propertyDetailsAdapter.updatePropertyTypes(propertyDetails)

        // Set the adapter to the RecyclerView
        binding.propertyTypesList.adapter = propertyDetailsAdapter
    }


    private fun setupPropertyDetails(){
        imageSlideAdapter = property?.images?.let { PropertyDetailsImageAdapter(it) }!!

        binding.viewPager.adapter = imageSlideAdapter

        binding.propertyName.text = property!!.title

        binding.propertyAddress.text = property!!.location.address

        binding.propertyDescription.text = property!!.description
    }


    private fun showMessageDialog() {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_message, null)

        // Initialize dialog elements
        val etMessage = dialogView.findViewById<EditText>(R.id.messageEditTextView)
        val btnSend = dialogView.findViewById<Button>(R.id.btnSubmit)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClose) // Add this line to find the close button

        // Build the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Close button action
        btnClose.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when close button is clicked
        }

        // Send button action
        btnSend.setOnClickListener {
            val message = etMessage.text.toString()
            if(message.isNotEmpty()){

                sendMessage(message)
            } else {

            }
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun sendMessage(message: String){
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            //observeViewModel(token, user.id, message)
            // Observe the view model to get transactions based on the user ID
            //observeViewModel(token, user.id)
        } else {
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }


//    // Method to observe the ViewModel for transaction-related data and status updates
//    private fun observeViewModel(token: String, userId: String, messageText: String) {
//        Log.d("token", "token: ${token}")
//
//
//        // Validate user input before sending data to the server
//        if (messageText.isEmpty() || chatId.isNullOrEmpty()) {
//            return
//        }
//
//        val newMessage = SendMessage(chatId = chatId!!, senderId = userId, text = messageText)
//        // Observe the status of the transaction fetching operation
//        chatController.status.observe(viewLifecycleOwner) { status ->
//            // Handle changes in the status (indicates success or failure)
//
//            // Check for timeout or inability to resolve host
//            // This observer implementation was adapted from stackoverflow
//            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
//            // Kevin Robatel
//            // https://stackoverflow.com/users/244702/kevin-robatel
//            if (status) {
//                // Success: Dismiss the progress dialog
//                //progressDialog.dismiss()
//                Log.d("status", "successful")
//
//            } else {
//                Log.d("status", "fail")
//// Failure: Dismiss the progress dialog
//                //progressDialog.dismiss()
//                // Optionally handle failure case (e.g., show an error message)
//            }
//        }
//
//        // Observe any messages from the ViewModel
//        chatController.message.observe(viewLifecycleOwner) { message ->
//            // Check for timeout or inability to resolve host
//            // This observer implementation was adapted from stackoverflow
//            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
//            // Kevin Robatel
//            // https://stackoverflow.com/users/244702/kevin-robatel
//
//            // Log the message for debugging purposes
//            Log.d("Valuations message", message)
//
//            // Check for specific messages that indicate a timeout or network issue
//            if (message == "timeout" || message.contains("Unable to resolve host")) {
//                // Show a timeout dialog and attempt to reconnect
//                Log.d("failed retrieval", "Retry...")
//
//                chatController.sendMessage(token, newMessage)
//
//            }
//        }
//        // Initial call to fetch all transactions for the user
//        chatController.sendMessage(token, newMessage)
//    }

    companion object {
        const val PROPERTY_DETAILS_ARG = "propertyDetails"

        // Factory method to create a new instance of this fragment with a list of messages and chatId
        @JvmStatic
        fun newInstance(property: Property) =
            PropertyDetails().apply {
                arguments = Bundle().apply {
                    putParcelable(PROPERTY_DETAILS_ARG, property)
                }
            }
    }
}



