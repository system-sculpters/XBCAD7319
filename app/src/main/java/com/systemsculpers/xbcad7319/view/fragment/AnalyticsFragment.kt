package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.AppConstants
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.AnalyticsController
import com.systemsculpers.xbcad7319.data.api.controller.PropertyController
import com.systemsculpers.xbcad7319.data.model.AnalyticsValue
import com.systemsculpers.xbcad7319.data.model.PropertyCount
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.data.model.PropertyCounts
import com.systemsculpers.xbcad7319.data.model.analytics.AnalyticsResponse
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentAnalyticsBinding
import com.systemsculpers.xbcad7319.databinding.FragmentBookmarksBinding
import com.systemsculpers.xbcad7319.view.adapter.AnalyticsAdapter
import com.systemsculpers.xbcad7319.view.adapter.GraphAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyAnalyticsAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import com.systemsculpers.xbcad7319.view.observer.AnalyticsObserver
import com.systemsculpers.xbcad7319.view.observer.BookmarksObserver

class AnalyticsFragment : Fragment() {

    // View binding object for accessing views in the layout
    private var _binding: FragmentAnalyticsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display properties
    private lateinit var adapter: AnalyticsAdapter

    private lateinit var graphAdapter: GraphAdapter

    private lateinit var analyticsController: AnalyticsController

    // User and token managers for managing user sessions and authentication
    private lateinit var tokenManager: TokenManager

    private lateinit var dialog: Dialogs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        analyticsController = ViewModelProvider(this).get(AnalyticsController::class.java)

        // Get instances of user and token managers
        tokenManager = TokenManager.getInstance(requireContext())

        adapter = AnalyticsAdapter{
                property ->
            // Replace the current fragment with the MessagesFragment
        }

        graphAdapter = GraphAdapter(requireContext(), binding.revenueChart, textColor())

        dialog = Dialogs()
        setUpRecyclerView()

        getAnalytics()
        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.analytics))
    }

    private fun setUpRecyclerView() {
        binding.analyticsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Use LinearLayout for layout
        binding.analyticsRecyclerView.setHasFixedSize(true) // Improve performance with fixed size
        binding.analyticsRecyclerView.adapter = adapter // Set the adapter to display transaction items
    }

    private fun getAnalytics() {
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            // Observe the view model to get transactions based on the user ID
            observeViewModel(token)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }


    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String) {
        // Show a progress dialog to indicate loading state
        //val progressDialog = dialog.showProgressDialog(requireContext())

        // Observe the status of the transaction fetching operation
        analyticsController.status.observe(viewLifecycleOwner) { status ->
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
        analyticsController.message.observe(viewLifecycleOwner) { message ->
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

                //progressDialog.dismiss()
                dialog.showTimeoutDialog(requireContext()) {
                    //dialog.showProgressDialog(requireContext())
                    analyticsController.getAnalytics(token)
                }
            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        analyticsController.analyticsResponse.observe(viewLifecycleOwner,
            AnalyticsObserver(this)
        )

        // Initial call to fetch all transactions for the user
        analyticsController.getAnalytics(token)
    }

    fun updateAnalytics(value: AnalyticsResponse) {
        graphAdapter.updateGraph(value)

        val analyticsValues = mutableListOf<AnalyticsValue>()

        val v1 = AnalyticsValue(R.drawable.baseline_attach_money_24,
            "R${AppConstants.formatNumberToShortForm(value.totalRevenueAmount)}", "Revenue", R.color.blue)

        val v2 = AnalyticsValue(R.drawable.baseline_person_outline_24,
            (value.users.users + value.users.agents).toString(), "Users", R.color.gold)

        val v3 = AnalyticsValue(R.drawable.rental_icon,
            (value.properties.Land + value.properties.House + value.properties.Rental).toString(), "Properties", R.color.green)

        val v4 = AnalyticsValue(R.drawable.baseline_calculate_24,
            (value.valuations.pending + value.valuations.completed).toString(), "Valuations", R.color.red)

        analyticsValues.add(v1)
        analyticsValues.add(v2)
        analyticsValues.add(v3)
        analyticsValues.add(v4)


        adapter.updateAnalytics(analyticsValues)
    }

    private fun textColor(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true)
        return typedValue.data
    }
}
