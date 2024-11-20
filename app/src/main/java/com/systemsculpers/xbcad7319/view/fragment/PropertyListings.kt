package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.PropertyController
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyListingsBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeFilterAdapter
import com.systemsculpers.xbcad7319.view.observer.PropertyListingObserver


class PropertyListings : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPropertyListingsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyTypeAdapter: PropertyTypeFilterAdapter

    private lateinit var adapter: PropertyAdapter

    private lateinit var propertyController: PropertyController

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var filteredPropertyList: MutableList<Property>

    private lateinit var propertyList: MutableList<Property>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentPropertyListingsBinding.inflate(inflater, container, false)
        propertyController = ViewModelProvider(this).get(PropertyController::class.java)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        filteredPropertyList = mutableListOf<Property>()

        propertyList = mutableListOf<Property>()

        adapter = PropertyAdapter(requireContext()){
                property ->

            if(property.status == "Sold"){
                val propertyDetailsFragment = SoldPropertyDetailsFragment.newInstance(property)
                changeCurrentFragment(propertyDetailsFragment)
            } else if( property.status == "For sale"){
                val propertyDetailsFragment = PropertyDetails.newInstance(property)
                changeCurrentFragment(propertyDetailsFragment)
            }
        }

        setPropertyTypes()

        setUpRecyclerView()

        getProperties()

        searchProperties()
        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.properties))
    }

    private fun searchProperties(){
        binding.searchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    filterProperties(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProperties(query: String) {
        filteredPropertyList.clear() // Clear the current filtered list
        // Filter properties based on the query
        filteredPropertyList.addAll(propertyList.filter { property ->
            property.title.contains(query, ignoreCase = true) // Assuming Property has a 'name' field
        })

        adapter.updateProperties(filteredPropertyList) // Update the adapter with filtered properties
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 3) // 3 icons per row
        //binding.properyTypesList.setHasFixedSize(false)

        // Adapter to display available colors and handle color selection
        propertyTypeAdapter = PropertyTypeFilterAdapter(requireContext()) { selectedPropertyType ->
            Log.d("SelectedCategory", "Selected Property Type: $selectedPropertyType")

            filterPropertiesByType(selectedPropertyType.name) // Filter based on selected type

        }

        binding.propertyTypesList.adapter = propertyTypeAdapter
    }

    private fun filterPropertiesByType(selectedType: String) {
        filteredPropertyList.clear() // Clear the filtered list before applying new filter
        // Filter properties based on the selected type
        filteredPropertyList.addAll(propertyList.filter { property ->
            property.propertyType.equals(selectedType, ignoreCase = true) // Assuming Property has a 'type' field
        })

        adapter.updateProperties(filteredPropertyList) // Update the adapter with filtered properties
    }

    private fun setUpRecyclerView() {
        binding.propertyRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // Use LinearLayout for layout
        binding.propertyRecyclerView.setHasFixedSize(true) // Improve performance with fixed size
        binding.propertyRecyclerView.adapter = adapter // Set the adapter to display transaction items
    }

    private fun getProperties() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            // Observe the view model to get transactions based on the user ID
            observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }


    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        // Show a progress dialog to indicate loading state

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
                Log.d("status", "successful")

            } else {
                Log.d("status", "fail")
                // Failure: Dismiss the progress dialog
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

                propertyController.getProperties(token, userId)

            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        propertyController.propertyList.observe(viewLifecycleOwner,
            PropertyListingObserver(adapter, this)
        )

        // Initial call to fetch all transactions for the user
        propertyController.getProperties(token, userId)
    }

    fun updateProperties(data: List<Property>) {
        propertyList.clear() // Clear the existing transactions
        propertyList.addAll(data) // Add new transactions to the list
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

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}