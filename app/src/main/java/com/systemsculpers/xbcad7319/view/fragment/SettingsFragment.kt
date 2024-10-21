package com.systemsculpers.xbcad7319.view.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentSearchLocationBinding
import com.systemsculpers.xbcad7319.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    // View binding object for accessing views in the layout
    private var _binding: FragmentSettingsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        binding.editProfile.setOnClickListener{

        }

        binding.changePassword.setOnClickListener{

        }

        binding.language.setOnClickListener{
            changeCurrentFragment(LanguageFragment())
        }

        binding.about.setOnClickListener{
            changeCurrentFragment(AboutFragment())
        }

        binding.logout.setOnClickListener{
            logout()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupListeners() {
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for notification enabled state
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for notification enabled state
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("theme_preference", isChecked).apply()
            applyTheme(isChecked)
        }


    }
    private fun applyTheme(theme: Boolean) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions
        when (theme) {
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Set light mode
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Set dark mode
        }
        // Restart the activity to apply the new theme settings
        activity?.recreate()
    }
    // Logs the user out by clearing tokens and signing out
    private fun logout() {
        tokenManager.clearToken() // Clear the stored token
        userManager.clearUser() // Clear user details
        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show() // Show logout message

        // Restart the MainActivity
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear previous activities
        startActivity(intent)
        requireActivity().finish() // Finish the current activity
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to prevent memory leaks
    }
}