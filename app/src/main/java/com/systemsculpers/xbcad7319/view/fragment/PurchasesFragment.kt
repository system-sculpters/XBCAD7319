package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.systemsculpers.xbcad7319.databinding.FragmentPurchasesBinding


class PurchasesFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPurchasesBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchasesBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }


}