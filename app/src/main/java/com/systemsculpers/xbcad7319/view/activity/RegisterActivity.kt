package com.systemsculpers.xbcad7319.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.AuthController
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.databinding.ActivityRegisterBinding
import com.systemsculpers.xbcad7319.databinding.ActivityWelcomeBinding
import com.systemsculpers.xbcad7319.view.custom.Dialogs

class RegisterActivity : AppCompatActivity() {
    // View binding for the activity layout, ensuring type-safe access to views
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: AuthController

    private lateinit var dialogs: Dialogs

    private var errorMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogs = Dialogs()

        // Initialize ViewModel for authentication operations
        auth = ViewModelProvider(this).get(AuthController::class.java)

        binding.tvSignUpPrompt.setOnClickListener{
            // Start the LoginActivity when the button is clicked
            startActivity(Intent(this, LoginActivity::class.java))
            // Close the WelcomeActivity so it cannot be returned to
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }

    }

    // Function to handle user login process
    private fun registerUser() {
        // Show a progress dialog to indicate loading state
        //val progressDialog = timeOutDialog.showProgressDialog(this)
        val progressDialog = dialogs.showProgressDialog(this)

        // Get email and password input from the respective EditText fields
        val fullName = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()

        // Log the email and password for debugging purposes
        Log.d("login", "$email $password")

        // Validate input; if not valid, dismiss the progress dialog and show an alert
        if (!validateInput(fullName, email, phoneNumber, password)) {
            progressDialog.dismiss() // Dismiss the progress dialog

            dialogs.showAlertDialog(this, errorMessage) // Show error message
            errorMessage = "" // Reset error message
            return // Exit the function if input is invalid
        }

        // Create a User object with the email and password
        val user = User(fullName = fullName, email = email, phoneNumber = phoneNumber, password = password)

        // Observe the authentication status
        auth.status.observe(this) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle status changes (success or failure)
            if (status) {
                // Update the progress dialog to indicate success
                //timeOutDialog.updateProgressDialog(this, progressDialog, "Login successful!", hideProgressBar = true)
                Log.d("login success", "success")
                startActivity(Intent(this, LoginActivity::class.java))

            } else {
                // Update the progress dialog to indicate failure
                //timeOutDialog.updateProgressDialog(this, progressDialog, "Login unsuccessful!", hideProgressBar = true)
                Log.d("login fail", "fail")

            }
        }

        // Observe authentication messages, such as connection issues or timeouts
        auth.message.observe(this) { message ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            Log.d("login fail", "fail: $message")

            // Check for timeout or inability to connect

            // Check for timeout or inability to connect
            if(message.contains("401")){
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()

            }
            else if (message == "timeout" || message.contains("Unable to resolve host")) {
                progressDialog.dismiss() // Dismiss the current dialog
                // Show a timeout dialog and attempt to reconnect
                dialogs.showTimeoutDialog(this) {
                    // Restart the progress dialog
                    dialogs.showProgressDialog(this)
                    auth.register(user) // Attempt to login again
                }
            }
        }

        // Perform the login API call
        auth.register(user) // Initiate login process
    }

    private fun validateInput(fullName: String, email: String, phoneNumber: String, password: String): Boolean {

        // Email validation
        if (fullName.isEmpty()) {
            Log.d("invalid", "FullName is empty")
            errorMessage += "${getString(R.string.empty_full_name)}\n"

        }
        // Email validation
        if (email.isEmpty()) {
            Log.d("invalid", "email is empty")
            errorMessage += "${getString(R.string.empty_email)}\n"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("invalid", "invalid email")
            errorMessage += "${getString(R.string.invalid_email_format)}\n"
        }

        // Phone number validation
        if (phoneNumber.isBlank()) {
            Log.d("invalid", "phone number is empty")
            errorMessage += "${getString(R.string.empty_phone_number)}\n"

        } else if (!phoneNumber.matches(Regex("^0[0-9]{9}\$"))) {
            Log.d("invalid", "invalid phone number")
            errorMessage += "${getString(R.string.invalid_phone_number_format)}\n"

        }

        // Password validation
        if (password.isBlank()) {
            errorMessage += "${getString(R.string.blank_password)}\n"
            Log.d("invalid", "password is empty")
        }

        return errorMessage.isBlank()
    }

}