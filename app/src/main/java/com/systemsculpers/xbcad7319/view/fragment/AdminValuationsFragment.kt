package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.databinding.FragmentAdminValuationsBinding
import com.systemsculpers.xbcad7319.databinding.FragmentAgentValuationsBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyDetailsAdapter
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter


class AdminValuationsFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentAdminValuationsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private lateinit var valuationsAdapter: ValuationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminValuationsBinding.inflate(inflater, container, false)

        valuationsAdapter = ValuationsAdapter{
            valuation ->  
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.valuations))
    }

}