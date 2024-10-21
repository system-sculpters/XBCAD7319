package com.systemsculpers.xbcad7319.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.databinding.FragmentLanguageBinding
import java.util.Locale


class LanguageFragment : Fragment() {
    // Backing property for binding, which is used to access the views in the Fragment
    private var _binding: FragmentLanguageBinding? = null

    // Public property to provide access to the binding while ensuring it is non-null
    private val binding get() = _binding!!

    // SharedPreferences instance to manage and persist the user's language preferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)

        // Set up the Spinner with English, Afrikaans, and Zulu
        val languages = listOf("English", "Afrikaans", "Zulu")
        binding.languageSpinner.setItems(languages)


        // Load saved language preference and set Spinner selection
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "English")
        binding.languageSpinner.selectItemByIndex(languages.indexOf(savedLanguage))

        // Handle language selection
        // Assuming 'binding.languageSpinner' is your PowerSpinnerView instance
        binding.languageSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            // newItem is the selected language
            saveLanguagePreference(newItem)
            setLocale(newItem)
        }

        return binding.root
    }

    //Saves the selected language preference to SharedPreferences.
    private fun saveLanguagePreference(language: String) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        with(sharedPreferences.edit()) {
            putString("selectedLanguage", language) // Store the selected language
            apply() // Apply changes asynchronously
        }
    }

    //Sets the application locale based on the selected language
    private fun setLocale(language: String) {
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
        //requireContext().createConfigurationContext(config) // Apply the configuration context
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        requireActivity().recreate()
        // Refresh the UI to reflect the new language
        //refreshUI() // Call to update the UI with the new locale
    }

    ///Refreshes the user interface by detaching and re-attaching the fragment.

    private fun refreshUI() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        parentFragmentManager.beginTransaction().detach(this).attach(this).commit() // Restart the fragment to refresh the UI
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}