package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.UserController
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentUpdateSelectUserBinding
import com.systemsculpers.xbcad7319.view.adapter.UsersAdapter
import com.systemsculpers.xbcad7319.view.observer.UsersObserver


class UpdateSelectUserFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentUpdateSelectUserBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private lateinit var userController: UserController
    private lateinit var adapter: UsersAdapter

    private lateinit var filteredUserList: MutableList<User>
    private lateinit var userList: MutableList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateSelectUserBinding.inflate(inflater, container, false)

        userController = ViewModelProvider(this).get(UserController::class.java)

        adapter = UsersAdapter{
                user -> onUserSelected(user)
        }

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        filteredUserList = mutableListOf<User>()
        userList = mutableListOf<User>()
        setUpRecyclerView()

        setUpUserDetails()

        searchUsers()
        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.select_users))
    }

    private fun searchUsers(){
        binding.searchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    filterUsers(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterUsers(query: String) {
        filteredUserList.clear() // Clear the current filtered list
        // Filter properties based on the query
        filteredUserList.addAll(userList.filter { user ->
            user.email.contains(query, ignoreCase = true) // Assuming Property has a 'name' field
        })

        adapter.updateUsers(filteredUserList) // Update the adapter with filtered properties
    }

    // Function to set up the RecyclerView for displaying transactions
    private fun setUpRecyclerView() {
        binding.usersList.layoutManager = LinearLayoutManager(requireContext()) // Use LinearLayout for layout
        binding.usersList.setHasFixedSize(true) // Improve performance with fixed size
        binding.usersList.adapter = adapter // Set the adapter to display transaction items
    }

    // Function to set up user details and observe the view model for transaction updates
    private fun setUpUserDetails() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            observeViewModel(token, user.id)
            // Observe the view model to get transactions based on the user ID
            //observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }

    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        // Show a progress dialog to indicate loading state

        // Observe the status of the transaction fetching operation
        userController.status.observe(viewLifecycleOwner) { status ->
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
        userController.message.observe(viewLifecycleOwner) { message ->
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

                userController.getUsersByRole(token)

            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        userController.userList.observe(viewLifecycleOwner,
            UsersObserver(adapter, null, null, this)
        )

        // Initial call to fetch all transactions for the user
        userController.getUsersByRole(token)
    }

    private fun onUserSelected(user: User) {
        // Do something with the selected location (e.g., return it to another fragment or show more details)
        Log.d("MapFragment", "Location selected: ${user}")

        val bundle = Bundle().apply {
            putParcelableArrayList("selected_images", arguments?.getParcelableArrayList<Uri>("selected_images"))
            putParcelable("location",  arguments?.getParcelable("location"))
            putParcelable("user", user)
            putParcelable("property", arguments?.getParcelable<Property>("property"))
        }
        val createPropertyFragment = UpdatePropertyFragment()
        createPropertyFragment.arguments = bundle

        changeCurrentFragment(createPropertyFragment)
    }

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

    fun updateUsers(value: List<User>) {
        userList.clear()
        userList.addAll(value)
    }
}