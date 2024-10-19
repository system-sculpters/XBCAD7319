package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.databinding.FragmentCreatePropertyBinding
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyListingsBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeFilterAdapter


class PropertyListings : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPropertyListingsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyTypeAdapter: PropertyTypeFilterAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentPropertyListingsBinding.inflate(inflater, container, false)

        setPropertyTypes()

        binding.locationRedirect.setOnClickListener{

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 3) // 3 icons per row
        //binding.properyTypesList.setHasFixedSize(false)

        // Adapter to display available colors and handle color selection
        propertyTypeAdapter = PropertyTypeFilterAdapter(requireContext()) { selectedPropertyType ->
            Log.d("SelectedCategory", "Selected Property Type: $selectedPropertyType")

            if(selectedPropertyType.name == getString(R.string.house) || selectedPropertyType.name == getString(R.string.rental)){

            }
            else if(selectedPropertyType.name == getString(R.string.land)){

            }
            else if(selectedPropertyType.name == getString(R.string.rental)){

            }
        }

        binding.propertyTypesList.adapter = propertyTypeAdapter
    }



    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}