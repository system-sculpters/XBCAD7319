package com.systemsculpers.xbcad7319.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.AuthController
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.ActivityLoginBinding
import com.systemsculpers.xbcad7319.view.custom.Dialogs

class LoginActivity : AppCompatActivity() {
    // View binding for the layout associated with this activity
    private lateinit var binding: ActivityLoginBinding

    // Controllers for handling authentication, token management, and user management
    private lateinit var auth: AuthController
    private lateinit var tokenManager: TokenManager
    private lateinit var userManager: UserManager

    private lateinit var dialogs: Dialogs

    private var errorMessage = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate the activity's layout using view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel for authentication operations
        auth = ViewModelProvider(this).get(AuthController::class.java)

        // Initialize TokenManager and UserManager for managing tokens and user data
        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)

        dialogs = Dialogs()
        // Set up click listener for the Sign In button
        binding.btnSignIn.setOnClickListener { loginUser() }

        binding.tvSignUpPrompt.setOnClickListener {
            // Start the LoginActivity when the button is clicked
            startActivity(Intent(this, RegisterActivity::class.java))
            // Close the WelcomeActivity so it cannot be returned to
            finish()
        }

    }

    // Function to handle user login process
    private fun loginUser() {
        // Show a progress dialog to indicate loading state
        val progressDialog = dialogs.showProgressDialog(this)

        // Get email and password input from the respective EditText fields
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        // Log the email and password for debugging purposes
        Log.d("login", "$email $password")

        // Validate input; if not valid, dismiss the progress dialog and show an alert
        if (!validateInput(email, password)) {
            progressDialog.dismiss() // Dismiss the progress dialog

            dialogs.showAlertDialog(this, errorMessage) // Show error message
            errorMessage = "" // Reset error message
            return // Exit the function if input is invalid
        }

        // Create a User object with the email and password
        val user = User(email = email, password = password)

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
                progressDialog.dismiss() // Dismiss the current dialog
                Log.d("login success", "success")
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                // Dismiss the dialog after a 2-second delay
//                Handler(Looper.getMainLooper()).postDelayed({
//                    //progressDialog.dismiss() // Dismiss the progress dialog
//                    // Navigate to MainActivity after login success
//                    finish() // Finish the current activity
//                }, 2000)
            } else {
                // Update the progress dialog to indicate failure
                //timeOutDialog.updateProgressDialog(this, progressDialog, "Login unsuccessful!", hideProgressBar = true)
                progressDialog.dismiss() // Dismiss the current dialog
                Log.d("login fail", "fail")
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()

                // Dismiss the dialog after a 2-second delay
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
            if(message == "timeout" || message.contains("Unable to resolve host")) {
                progressDialog.dismiss() // Dismiss the current dialog
                // Show a timeout dialog and attempt to reconnect
                dialogs.showTimeoutDialog(this) {
                    // Restart the progress dialog
                    dialogs.showProgressDialog(this)
                    auth.login(user) // Attempt to login again
                }
            }
        }

        // Observe user data after successful authentication
        auth.userData.observe(this) { userData ->
            // Save the token and user data using the TokenManager and UserManager
            tokenManager.saveToken(userData.token)
            userManager.saveUser(userData)
            userManager.savePassword(password) // Save the user's password (presumably hashed)
        }

        // Perform the login API call
        auth.login(user) // Initiate login process
    }

    private fun validateInput( email: String, password: String): Boolean {

        // Email validation
        if (email.isEmpty()) {
            Log.d("invalid", "email is empty")
            errorMessage += "${getString(R.string.empty_email)}\n"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("invalid", "invalid email")
            errorMessage += "${getString(R.string.invalid_email_format)}\n"
        }

        // Password validation
        if (password.isBlank()) {
            errorMessage += "${getString(R.string.blank_password)}\n"
            Log.d("invalid", "password is empty")
        }

        return errorMessage.isBlank()
    }
}