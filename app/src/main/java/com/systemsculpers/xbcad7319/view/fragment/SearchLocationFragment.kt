package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.MapController
import com.systemsculpers.xbcad7319.databinding.FragmentCreatePropertyBinding
import com.systemsculpers.xbcad7319.databinding.FragmentSearchLocationBinding


class SearchLocationFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentSearchLocationBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // ViewModel responsible for managing and processing the category data.
    private lateinit var mapController: MapController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchLocationBinding.inflate(inflater, container, false)

        mapController = ViewModelProvider(this).get(MapController::class.java)

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
}