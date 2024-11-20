package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.systemsculpers.xbcad7319.databinding.FragmentAgentValuationsBinding
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyDetailsBinding
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import com.systemsculpers.xbcad7319.view.observer.ValuationsObserver


class AgentValuationsFragment : Fragment() {

    // View binding object for accessing views in the layout
    private var _binding: FragmentAgentValuationsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var valuationsAdapter: ValuationsAdapter

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    // ViewModel for managing transaction-related data
    private lateinit var valuationController: ValuationController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgentValuationsBinding.inflate(inflater, container, false)

        valuationController = ViewModelProvider(this).get(ValuationController::class.java)

        valuationsAdapter = ValuationsAdapter{
                valuation -> showDialog(valuation)
        }

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Set up the RecyclerView to display transactions
        setUpRecyclerView()

        // Load user details and set up view model observers
        setUpUserDetails()
        // Inflate the layout for this fragment
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

                valuationController.getValuations(token, userId)

            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        valuationController.valuationList.observe(viewLifecycleOwner,
            ValuationsObserver(valuationsAdapter)
        )

        // Initial call to fetch all transactions for the user
        valuationController.getValuations(token, userId)
    }

    private fun showDialog(valuation: Valuation) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.update_role_item, null)
        val roleSpinner = dialogView.findViewById<PowerSpinnerView>(R.id.roleSpinner)
        val updateBtn =  dialogView.findViewById<Button>(R.id.btnUpdate)
        val email = dialogView.findViewById<TextView>(R.id.email)

        email.text = getString(R.string.valuation)

        var status = ""

        val statuses = listOf("complete", "pending")

        roleSpinner.setItems(statuses)

        roleSpinner.selectItemByIndex(statuses.indexOf(valuation.status))

        roleSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            // newItem is the selected language
            status = newItem
        }

        if(status == valuation.status){
            return
        }

        updateBtn.setOnClickListener {
            val token = tokenManager.getToken() // Retrieve the authentication token

            if (token != null) {
                updateValuation(token,valuation.id, status)
            } else {
                startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
                // Handle case when the token is not available (e.g., show error or redirect)
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }

    private fun updateValuation(token: String, id: String, status: String) {
        val progressDialog = Dialogs().showProgressDialog(requireContext())

        val valuation = Valuation(status = status)
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
                Dialogs().updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_successful), hideProgressBar = true)
                progressDialog.dismiss()

            } else {
                Log.d("status", "fail")
                Dialogs().updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_fail), hideProgressBar = true)
                progressDialog.dismiss()            }
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
                    valuationController.updateValuation(token, id, valuation)
                }
            }
        }
        // Initial call to fetch all transactions for the user
        valuationController.updateValuation(token, id, valuation)
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