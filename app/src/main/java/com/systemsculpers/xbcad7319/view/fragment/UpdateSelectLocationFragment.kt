package com.systemsculpers.xbcad7319.view.fragment

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
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.MapController
import com.systemsculpers.xbcad7319.data.model.LocationResult
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.databinding.FragmentSearchLocationBinding
import com.systemsculpers.xbcad7319.databinding.FragmentUpdateSelectLocationBinding
import com.systemsculpers.xbcad7319.view.adapter.LocationAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateSelectLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateSelectLocationFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentUpdateSelectLocationBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // ViewModel responsible for managing and processing the category data.
    private lateinit var mapController: MapController

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateSelectLocationBinding.inflate(inflater, container, false)

        mapController = ViewModelProvider(this).get(MapController::class.java)

        locationAdapter = LocationAdapter { selectedLocation ->
            onLocationSelected(selectedLocation)
        }

        binding.searchResultsList.adapter = locationAdapter
        binding.searchResultsList.layoutManager = LinearLayoutManager(requireContext())

        mapController.locationList.observe(viewLifecycleOwner) { locations ->
            // Update the adapter with the new list
            if(locations == null){
                locationAdapter.updateLocation(listOf())
            } else {
                locationAdapter.updateLocation(locations)
            }
        }

        // Observe status and message if needed
        mapController.status.observe(viewLifecycleOwner) { status ->
            // Handle status updates (e.g., show loading or error)
        }


        binding.searchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    mapController.fetchSuggestions(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        // Inflate the layout for this fragment
        return  binding.root
    }

    private fun onLocationSelected(location: LocationResult) {
        // Do something with the selected location (e.g., return it to another fragment or show more details)
        Log.d("MapFragment", "Location selected: ${location}")

        val bundle = Bundle().apply {
            putParcelableArrayList("selected_images", arguments?.getParcelableArrayList<Uri>("selected_images"))
            putParcelable("location", location)
            putParcelable("user", arguments?.getParcelable("user"))
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
}