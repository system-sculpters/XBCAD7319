package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.databinding.FragmentSearchLocationBinding
import com.systemsculpers.xbcad7319.databinding.FragmentViewOnMapBinding
import org.osmdroid.config.Configuration


class ViewOnMapFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentViewOnMapBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

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
        binding.mapView.controller.setCenter(org.osmdroid.util.GeoPoint(-25.7675864, 28.0961602))
        // lat -25.7675864
        //                                                                                                    long 28.0961602
        return binding.root
    }


}