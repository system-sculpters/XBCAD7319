package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skydoves.powerspinner.PowerSpinnerView
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.ValuationController
import com.systemsculpers.xbcad7319.data.model.Valuation
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentValuationsBinding
import com.systemsculpers.xbcad7319.view.adapter.UserValuationsAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import com.systemsculpers.xbcad7319.view.observer.UserValuationsObserver


class ValuationsFragment : Fragment() {

    // View binding object for accessing views in the layout
    private var _binding: FragmentValuationsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var valuationsAdapter: UserValuationsAdapter

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    // ViewModel for managing transaction-related data
    private lateinit var valuationController: ValuationController
    private lateinit var dialog: Dialogs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValuationsBinding.inflate(inflater, container, false)

        valuationController = ViewModelProvider(this).get(ValuationController::class.java)

        valuationsAdapter = UserValuationsAdapter{
                valuation ->
        }

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        dialog = Dialogs()

        // Set up the RecyclerView to display transactions
        setUpRecyclerView()

        // Load user details and set up view model observers
        setUpUserDetails()
        // Inflate the layout for this fragment

        binding.submitButton.setOnClickListener {
            changeCurrentFragment(CreateValuationFragment())
        }

        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.valuations))
    }

    // Function to set up the RecyclerView for displaying transactions
    private fun setUpRecyclerView() {
        binding.valuationsList.layoutManager = LinearLayoutManager(requireContext()) // Use LinearLayout for layout
        binding.valuationsList.setHasFixedSize(true) // Improve performance with fixed size
        binding.valuationsList.adapter = valuationsAdapter // Set the adapter to display transaction items
    }

    // Function to handle navigation to the transaction details screen
    private fun redirectToDetails(valuation: Valuation) {
        // Create a new instance of UpdateTransactionFragment to display transaction details
//        val transactionDetailsFragment = UpdateTransactionFragment()
//        val bundle = Bundle()
//        bundle.putParcelable("transaction", transaction) // Pass the selected transaction as an argument
//        transactionDetailsFragment.arguments = bundle
//
//        // Navigate to the UpdateTransactionFragment
//        changeCurrentFragment(transactionDetailsFragment)
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
        }
    }


    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        // Show a progress dialog to indicate loading state
        val progressDialog = dialog.showProgressDialog(requireContext())

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
                progressDialog.dismiss()
                Log.d("status", "successful")

            } else {
                Log.d("status", "fail")
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

                valuationController.getUserValuations(token, userId)

            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        valuationController.valuationList.observe(viewLifecycleOwner,
            UserValuationsObserver(valuationsAdapter)
        )

        // Initial call to fetch all transactions for the user
        valuationController.getUserValuations(token, userId)
    }



    // Method to change the current fragment displayed in the UI
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki

        // Start a new fragment transaction
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment) // Replace the current fragment with the new one
            .addToBackStack(null) // Add the transaction to the back stack for navigation
            .commit() // Commit the transaction to apply changes
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}