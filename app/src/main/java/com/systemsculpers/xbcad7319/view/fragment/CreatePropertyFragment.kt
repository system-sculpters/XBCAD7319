package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentCreatePropertyBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter


class CreatePropertyFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentCreatePropertyBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyTypeAdapter: PropertyTypeAdapter

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private var propType: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentCreatePropertyBinding.inflate(inflater, container, false)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        setPropertyTypes()

        binding.locationRedirect.setOnClickListener{

        }

        binding.uploadImageRedirect.setOnClickListener{

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 3) // 3 icons per row
        //binding.properyTypesList.setHasFixedSize(false)

        // Adapter to display available colors and handle color selection
        propertyTypeAdapter = PropertyTypeAdapter(requireContext()) { selectedPropertyType ->
            Log.d("SelectedCategory", "Selected Property Type: $selectedPropertyType")
            propType = selectedPropertyType.name

            if(selectedPropertyType.name == getString(R.string.house) || selectedPropertyType.name == getString(R.string.rental)){
                binding.noOfBedrooms.visibility = View.VISIBLE
                binding.noOfBathrooms.visibility = View.VISIBLE
                binding.noOfParking.visibility = View.VISIBLE
            }
            else if(selectedPropertyType.name == getString(R.string.land)){
                binding.noOfBedrooms.visibility = View.GONE
                binding.noOfBathrooms.visibility = View.GONE
                binding.noOfParking.visibility = View.GONE
            }
        }

        binding.propertyTypesList.adapter = propertyTypeAdapter
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

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}