package com.systemsculpers.xbcad7319.view.fragment

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
import com.systemsculpers.xbcad7319.data.api.controller.AuthController
import com.systemsculpers.xbcad7319.data.api.controller.ValuationController
import com.systemsculpers.xbcad7319.data.model.Valuation
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentAdminValuationsBinding
import com.systemsculpers.xbcad7319.databinding.FragmentCreateValuationBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeFilterAdapter
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import com.systemsculpers.xbcad7319.view.observer.ValuationsObserver


class CreateValuationFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentCreateValuationBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyTypeAdapter: PropertyTypeAdapter

    private lateinit var valuationController: ValuationController

    private var type = ""

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private var errorMessage = ""
    private lateinit var dialog: Dialogs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateValuationBinding.inflate(inflater, container, false)

        valuationController = ViewModelProvider(this).get(ValuationController::class.java)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        dialog = Dialogs()

        setPropertyTypes()

        binding.submitButton.setOnClickListener {
            setUpUserDetails()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 3) // 3 icons per row

        // Adapter to display available colors and handle color selection
        propertyTypeAdapter = PropertyTypeAdapter(requireContext()) { selectedPropertyType ->
            Log.d("SelectedCategory", "Selected Property Type: $selectedPropertyType")
            type = selectedPropertyType.name
        }

        binding.propertyTypesList.adapter = propertyTypeAdapter
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.free_valuation))
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

    // Function to set up user details and observe the view model for transaction updates
    private fun setUpUserDetails() {
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
        val progressDialog = dialog.showProgressDialog(requireContext())

        Log.d("token", "token: ${token}")
        val propertyName = binding.propertyName.text.toString()
        val location = binding.location.text.toString()
        val propertyPrice = binding.propertyPrice.text.toString()
        val description = binding.description.text.toString()


        // Validate user input before sending data to the server
        if (!validateValuationData(propertyName, location, propertyPrice, description, type)) {
            progressDialog.dismiss()
            dialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        val newValuation = Valuation(
            userId = userId, propertyType = type, location = location, price = propertyPrice.toDouble(), description = description)
        // Observe the status of the transaction fetching operation
        valuationController.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // Success: Dismiss the progress dialog
                dialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.create_valuation_successful), hideProgressBar = true)

                progressDialog.dismiss()

                Toast.makeText(requireContext(), "valuation request created", Toast.LENGTH_LONG).show()

                changeCurrentFragment(ValuationsFragment())
                Log.d("status", "successful")

            } else {
                Log.d("status", "fail")
                dialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.create_valuation_failed), hideProgressBar = true)
                Toast.makeText(requireContext(), "valuation request failed", Toast.LENGTH_LONG).show()

                // Failure: Dismiss the progress dialog
                progressDialog.dismiss()
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        valuationController.message.observe(viewLifecycleOwner) { message ->
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

                progressDialog.dismiss()
                Dialogs().showTimeoutDialog(requireContext()) {
                    Dialogs().showProgressDialog(requireContext())
                    valuationController.createValuation(token, newValuation)
                }
            }
        }

        // Initial call to fetch all transactions for the user
        valuationController.createValuation(token, newValuation)
    }

    private fun validateValuationData(propertyName: String, location: String, propertyPrice: String, description: String, type: String): Boolean {
        var errors = 0

        if(propertyName.isEmpty()){
            Log.d("propertyName", "propertyName is empty")
            errorMessage += "${getString(R.string.enter_valuation_name)}\n"

            errors += 1
        }
        if(location.isEmpty()){
            Log.d("location", "location is empty")
            errorMessage += "${getString(R.string.select_location)}\n"
            errors += 1
        }
        if(propertyPrice.isEmpty()){
            errorMessage += "${getString(R.string.enter_price)}\n"
            Log.d("propertyPrice", "propertyPrice is empty")
            errors += 1
        }

        if(description.isEmpty()){
            Log.d("description", "description is empty")
            errorMessage += "${getString(R.string.enter_description)}\n"
            errors += 1
        }
        if(type.isEmpty() || type == ""){
            errorMessage += "${getString(R.string.enter_property_type)}\n"
            Log.d("type", "type is empty")
            errors += 1
        }
        return errors == 0
    }


    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}