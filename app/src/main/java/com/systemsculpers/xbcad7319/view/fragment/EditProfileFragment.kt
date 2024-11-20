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
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.UserController
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentEditProfileBinding
import com.systemsculpers.xbcad7319.databinding.FragmentUsersBinding
import com.systemsculpers.xbcad7319.view.adapter.UsersAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import com.systemsculpers.xbcad7319.view.observer.UsersObserver


class EditProfileFragment : Fragment() {

    // View binding object for accessing views in the layout
    private var _binding: FragmentEditProfileBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private lateinit var userController: UserController

    private lateinit var dialogs: Dialogs
    private var errorMessage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        userController = ViewModelProvider(this).get(UserController::class.java)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        dialogs = Dialogs()
        loadProfileData()

        binding.btnSave.setOnClickListener { setUpUserDetails() }

        binding.btnDelete.setOnClickListener { deleteDialog() }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.edit_profile))
    }

    // Load the user's profile data and display it in the UI
    private fun loadProfileData() {
        val user = userManager.getUser() // Get the current user

        // Retrieve user details
        val username = user.fullName
        val email = user.email
        val phoneNumber = user.phoneNumber // Get the stored password

        // Set the retrieved values to the corresponding UI elements
        binding.username.setText(username)
        binding.email.setText(email)
        binding.etPhoneNumber.setText(phoneNumber)
    }

    // Function to set up user details and observe the view model for transaction updates
    private fun setUpUserDetails() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            observeViewModel(token, user)
            // Observe the view model to get transactions based on the user ID
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }

    private fun deleteDialog() {
        val token = tokenManager.getToken() // Retrieve the authentication token
        val user = userManager.getUser() // Get the current user


        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete your account?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Handle the delete action here

            if (token != null) {
                deleteProfile(token, user.id, dialog)
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

    private fun deleteProfile(token: String, userId: String, dialog: DialogInterface) {
        val progressDialog = dialogs.showProgressDialog(requireContext())

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
                dialogs.updateProgressDialog(requireContext(), progressDialog, getString(R.string.delete_successful), hideProgressBar = true)
                progressDialog.dismiss()
                dialog.dismiss()
                userManager.clearUser()
                tokenManager.clearToken()
                Toast.makeText(requireContext(), "user deleted", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity

            } else {
                dialogs.updateProgressDialog(requireContext(), progressDialog, getString(R.string.delete_failed), hideProgressBar = true)
                dialog.dismiss()
                Log.d("status", "fail")
                progressDialog.dismiss()
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

                progressDialog.dismiss()
                Dialogs().showTimeoutDialog(requireContext()) {
                    Dialogs().showProgressDialog(requireContext())
                    userController.deleteUser(token, userId)
                }
            }
        }

        // Initial call to fetch all transactions for the user
        userController.deleteUser(token, userId)
    }

    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, user: User) {
        // Show a progress dialog to indicate loading state
        val progressDialog = dialogs.showProgressDialog(requireContext())

        val fullName = binding.username.text.toString()
        val email = binding.email.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()

        // Log the email and password for debugging purposes

        // Validate input; if not valid, dismiss the progress dialog and show an alert
        if (!validateInput(fullName, email, phoneNumber)) {
            progressDialog.dismiss() // Dismiss the progress dialog
            dialogs.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return // Exit the function if input is invalid
        }

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
                dialogs.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_successful), hideProgressBar = true)
                progressDialog.dismiss()
                Log.d("status", "successful")

                val updatedUser = User(id = user.id, email = email, role = user.role, fullName = fullName, phoneNumber = phoneNumber)
                userManager.saveUser(updatedUser)

                binding.username.setText(fullName)
                binding.email.setText(email)
                binding.etPhoneNumber.setText(phoneNumber)

            } else {
                dialogs.updateProgressDialog(requireContext(), progressDialog, getString(R.string.update_fail), hideProgressBar = true)
                progressDialog.dismiss()
                Log.d("status", "fail")
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

                progressDialog.dismiss()
                Dialogs().showTimeoutDialog(requireContext()) {
                    Dialogs().showProgressDialog(requireContext())
                    userController.updateUserDetails(token, user.id, user)
                }
            }
        }
        // Initial call to fetch all transactions for the user
        userController.updateUserDetails(token, user.id, user)
    }

    private fun validateInput(fullName: String, email: String, phoneNumber: String): Boolean {
        var errorMessage = 0

        // Email validation
        if (fullName.isEmpty()) {
            Log.d("invalid", "FullName is empty")
            errorMessage += 1
        }
        // Email validation
        if (email.isEmpty()) {
            Log.d("invalid", "email is empty")
            errorMessage += 1
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("invalid", "invalid email")
            errorMessage += 1
        }

        // Phone number validation
        if (phoneNumber.isBlank()) {
            Log.d("invalid", "phone number is empty")
            errorMessage += 1
        } else if (!phoneNumber.matches(Regex("^0[0-9]{9}\$"))) {
            Log.d("invalid", "invalid phone number")
            errorMessage += 1
        }


        return errorMessage == 0
    }
}