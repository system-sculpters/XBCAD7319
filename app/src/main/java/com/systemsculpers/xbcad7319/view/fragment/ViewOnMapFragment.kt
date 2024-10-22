package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.systemsculpers.xbcad7319.data.model.Location
import com.systemsculpers.xbcad7319.databinding.FragmentViewOnMapBinding
import org.osmdroid.config.Configuration


class ViewOnMapFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentViewOnMapBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private var location: Location = Location()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewOnMapBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        // Initialize OSMDroid Configuration
        Configuration.getInstance().load(requireContext(), android.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()))


        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(15.0) // Set initial zoom level

        // Set the initial position of the map (for example, latitude and longitude of New York City)
        binding.mapView.controller.setCenter(org.osmdroid.util.GeoPoint(location.latitude, location.longitude))
        // lat -25.7675864
        //                                                                                                    long 28.0961602
        return binding.root
    }

    companion object {
        const val LOCATION_ARG = "location"

        // Factory method to create a new instance of this fragment with a list of messages and chatId
        @JvmStatic
        fun newInstance(location: Location) =
            ViewOnMapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LOCATION_ARG, location)
                }
            }
    }
}