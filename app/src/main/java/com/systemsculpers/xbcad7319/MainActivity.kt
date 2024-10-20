package com.systemsculpers.xbcad7319

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.systemsculpers.xbcad7319.data.api.controller.AuthController
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.ActivityMainBinding
import com.systemsculpers.xbcad7319.view.activity.WelcomeActivity
import com.systemsculpers.xbcad7319.view.fragment.AgentValuationsFragment
import com.systemsculpers.xbcad7319.view.fragment.ChatsFragment
import com.systemsculpers.xbcad7319.view.fragment.CreatePropertyFragment
import com.systemsculpers.xbcad7319.view.fragment.CreateValuationFragment
import com.systemsculpers.xbcad7319.view.fragment.MessagesFragment
import com.systemsculpers.xbcad7319.view.fragment.PropertyDetails
import com.systemsculpers.xbcad7319.view.fragment.PropertyListings
import com.systemsculpers.xbcad7319.view.fragment.SearchLocationFragment
import com.systemsculpers.xbcad7319.view.fragment.SettingsFragment
import com.systemsculpers.xbcad7319.view.fragment.UploadImagesFragment
import com.systemsculpers.xbcad7319.view.fragment.ViewOnMapFragment
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // Binding object for activity_main layout to access UI elements
    private lateinit var binding: ActivityMainBinding

    // DrawerLayout to manage the navigation drawer
    private lateinit var drawerLayout: DrawerLayout

    // NavigationView to handle navigation item selection
    private lateinit var navigationView: NavigationView

    // SharedPreferences for storing user preferences
    private lateinit var sharedPreferences: SharedPreferences

    // TokenManager to manage user authentication tokens
    private lateinit var tokenManager: TokenManager

    // UserManager to manage user details
    private lateinit var userManager: UserManager

    // AuthController to handle authentication-related actions
    private lateinit var auth: AuthController


    override fun onCreate(savedInstanceState: Bundle?) {
        //super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve the saved language from SharedPreferences
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "English")

        // Apply the saved language
        setAppLocale(savedLanguage ?: "English")

        super.onCreate(savedInstanceState)

        // Inflate the binding layout and set it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Determine if the device is in dark mode to tint icons accordingly
        // https://medium.com/naukri-engineering/implement-dark-theme-support-for-android-application-using-kotlin-665060d269b6
        // Nitin Berwal
        // https://medium.com/@nitinberwal89
        // This dark mode implementation was adapted from mdeium
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        //tintIconForDarkMode(findViewById(R.id.back_button), isDarkMode)
        //tintIconForDarkMode(findViewById(R.id.nav_drawer_opener), isDarkMode)

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        //navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Initialize managers for token and user management
        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)
        auth = ViewModelProvider(this).get(AuthController::class.java)

        // Check if the user is logged in and set up navigation accordingly
        if (isLoggedIn()) {
            setupBottomNavigation()  // Initialize bottom navigation
        } else {
            navigateToWelcome()      // Redirect to welcome screen
        }


        setupBottomNavigation()
    }
    private fun setupBottomNavigation() {
        val userRole = userManager.getUser().role // Retrieve the user's role (admin, agent, user)

        Log.d("user", "user: ${userRole}")
        // Load a different menu for each role
        when (userRole) {
            "admin" -> {
                binding.bottomNavigation.menu.clear() // Clear any previous menu
                binding.bottomNavigation.inflateMenu(R.menu.admin_bottom_menu) // Load admin-specific menu
            }
            "agent" -> {
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.agent_bottom_menu) // Load agent-specific menu
                //binding.bottomNavigation.menu.
            }
            "user" -> {
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.user_bottom_menu) // Load user-specific menu
            }
            else -> {
                Log.e("MainActivity", "Invalid user role")
            }
        }
        //changeCurrentFragment(CreatePropertyFragment())
        changeCurrentFragment(SearchLocationFragment())

        // Code for when a different button is pressed on the navigation menu

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    changeCurrentFragment(MessagesFragment())
                }
                R.id.transactions -> {
                    changeCurrentFragment(PropertyListings())
                }
                R.id.settings ->{
                    logOut()

                }
                R.id.agent_home ->{

                }
                R.id.agent_valuations ->{
                    changeCurrentFragment(AgentValuationsFragment())
                }
                R.id.user_valuations ->{
                    changeCurrentFragment(CreateValuationFragment())
                }
                R.id.chat->{
                    changeCurrentFragment(ChatsFragment())
                }
                R.id.properties ->{
                    changeCurrentFragment(UploadImagesFragment())
                }
            }
            true
        }
    }
    private fun changeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)

            commit()
        }
    }


    // Navigates to the authentication screens if the user is not logged in
    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java)) // Start WelcomeActivity
        finish() // Finish the current activity
    }

    // Checks if the user is currently logged in
    private fun isLoggedIn(): Boolean {
        val token = tokenManager.getToken() // Retrieve the authentication token
        val expirationTime = tokenManager.getTokenExpirationTime() // Get the token expiration time
        return token != null && !AppConstants.isTokenExpired(expirationTime) // Check if the token is valid
    }

    // Logs the user out by clearing tokens and signing out
    private fun logOut() {
        tokenManager.clearToken() // Clear the stored token
        userManager.clearUser() // Clear user details
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show() // Show logout message
        startActivity(Intent(this, MainActivity::class.java)) // Restart the MainActivity
        finish() // Finish the current activity
    }

    private fun setAppLocale(language: String) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // Determine the locale code based on the selected language
        val localeCode = when (language) {
            "Afrikaans" -> "af" // Afrikaans language code
            "Zulu" -> "zu" // Zulu language code
            else -> "en" // Default to English if no match is found
        }

        val locale = Locale(localeCode) // Create a new Locale object
        Locale.setDefault(locale) // Set the default locale

        val config = Configuration() // Create a new Configuration object
        config.setLocale(locale) // Set the locale in the configuration

        resources.updateConfiguration(config, resources.displayMetrics)
    }

}