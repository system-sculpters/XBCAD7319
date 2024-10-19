package com.systemsculpers.xbcad7319.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    // View binding for the activity layout, ensuring type-safe access to views
    private lateinit var binding: ActivityWelcomeBinding

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflate the layout using view binding and set it as the content view
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a click listener on the continue button
        binding.loginRedirect.setOnClickListener {
            // Start the LoginActivity when the button is clicked
            startActivity(Intent(this, LoginActivity::class.java))
            // Close the WelcomeActivity so it cannot be returned to
            finish()
        }

        binding.signUp.setOnClickListener{
            // Start the LoginActivity when the button is clicked
            startActivity(Intent(this, RegisterActivity::class.java))
            // Close the WelcomeActivity so it cannot be returned to
            finish()
        }
    }
}