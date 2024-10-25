package com.systemsculpers.xbcad7319.view.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.BookmarkController
import com.systemsculpers.xbcad7319.data.api.controller.ChatController
import com.systemsculpers.xbcad7319.data.api.controller.PropertyController
import com.systemsculpers.xbcad7319.data.model.Bookmark
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentAgentPropertyDetailsBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyDetailsAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyDetailsImageAdapter


class AgentPropertyDetailsFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentAgentPropertyDetailsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private var property: Property? = Property()

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyDetailsAdapter: PropertyDetailsAdapter

    private lateinit var imageSlideAdapter: PropertyDetailsImageAdapter

    private lateinit var chatController: ChatController

    private lateinit var bookmarkController: BookmarkController

    private lateinit var propertyController: PropertyController

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
        _binding = FragmentAgentPropertyDetailsBinding.inflate(inflater, container, false)

        propertyDetailsAdapter = PropertyDetailsAdapter()

        chatController = ViewModelProvider(this).get(ChatController::class.java)

        bookmarkController = ViewModelProvider(this).get(BookmarkController::class.java)

        propertyController = ViewModelProvider(this).get(PropertyController::class.java)
        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        setupPropertyDetails()
        setPropertyTypes()


        property?.let { setBookmarked(it.isBookmarked) }

        val viewOnMapFragment = property?.let { ViewOnMapFragment.newInstance(it.location) }


        binding.seeOnMaps.setOnClickListener{
            if (viewOnMapFragment != null) {
                changeCurrentFragment(viewOnMapFragment)
            }
        }

        binding.bookmark.setOnClickListener {
            bookmarkAction()
        }

        binding.delete.setOnClickListener {
            deleteDialog()
        }

//        binding.update.setOnClickListener {
//            val bundle = Bundle().apply {
//                putParcelable("property", property)
//            }
//            val createPropertyFragment = UpdatePropertyFragment()
//            createPropertyFragment.arguments = bundle
//
//            changeCurrentFragment(createPropertyFragment)
//        }

        return binding.root
    }

    private fun deleteDialog() {
        val token = tokenManager.getToken() // Retrieve the authentication token


        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this property?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Handle the delete action here

            if (token != null) {
                deleteProperty(token, property?.id, dialog)
            } else {
                dialog.dismiss()
                startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
                // Handle case when the token is not available (e.g., show error or redirect)
            }
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setBookmarked(isBookmarked: Boolean){
        if(isBookmarked){

            binding.bookmark.setImageResource(R.drawable.baseline_bookmark_checked)
        } else{
            binding.bookmark.setImageResource(R.drawable.baseline_bookmark_border_24)
        }
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 4) // 3 icons per row

        // Create an empty list for property details
        val propertyDetails: MutableList<PropertyType> = mutableListOf()

        // Check if the property type is "land"
        if (property?.propertyType == "Land") {
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

    private fun bookmarkAction(){
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {

            if(property!!.isBookmarked){
                removeBookmark(token, user.id, property!!.id)
            } else{
                addBookmark(token, user.id, property!!.id)
            }
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }

    private fun addBookmark(token: String, userId: String, propertyId: String) {
        // Show a progress dialog to indicate loading state

        val newBookmark = Bookmark(propertyId = propertyId, userId = userId)
        // Observe the status of the transaction fetching operation
        bookmarkController.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Success: Dismiss the progress dialog
                //progressDialog.dismiss()
                Log.d("status", "successful")
                property!!.isBookmarked = true
                setBookmarked(true)

            } else {
                Log.d("status", "fail")
                //progressDialog.dismiss()
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        bookmarkController.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Log the message for debugging purposes
            Log.d("Valuations message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                Log.d("failed retrieval", "Retry...")

                bookmarkController.bookmarkProperty(token, propertyId, newBookmark)

            }
        }

        // Initial call to fetch all transactions for the user
        bookmarkController.bookmarkProperty(token, propertyId, newBookmark)
    }

    private fun removeBookmark(token: String, userId: String, propertyId: String) {

        // Observe the status of the transaction fetching operation
        bookmarkController.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Success: Dismiss the progress dialog
                //progressDialog.dismiss()
                Log.d("status", "successful")
                property!!.isBookmarked = false
                setBookmarked(false)

            } else {
                Log.d("status", "fail")
                //progressDialog.dismiss()
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        bookmarkController.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Log the message for debugging purposes
            Log.d("Valuations message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                Log.d("failed retrieval", "Retry...")

                bookmarkController.unBookmarkProperty(token, propertyId, userId)

            }
        }

        // Initial call to fetch all transactions for the user
        bookmarkController.unBookmarkProperty(token, propertyId, userId)
    }


    // Helper function to change the current fragment in the activity.
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun deleteProperty(token: String, propertyId: String?, dialog: DialogInterface) {

        // Observe the status of the transaction fetching operation
        propertyController.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Success: Dismiss the progress dialog
                //progressDialog.dismiss()
                Toast.makeText(requireContext(), "Property deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                changeCurrentFragment(AgentPropertiesFragment())

            } else {
                Log.d("status", "fail")
                //progressDialog.dismiss()
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        propertyController.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Log the message for debugging purposes
            Log.d("Valuations message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                Log.d("failed retrieval", "Retry...")

                propertyController.deleteProperty(token, propertyId!!)

            }
        }

        // Initial call to fetch all transactions for the user
        propertyController.deleteProperty(token, propertyId!!)
    }


    companion object {
        const val PROPERTY_DETAILS_ARG = "propertyDetails"

        // Factory method to create a new instance of this fragment with a list of messages and chatId
        @JvmStatic
        fun newInstance(property: Property) =
            AgentPropertyDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PROPERTY_DETAILS_ARG, property)
                }
            }
    }
}