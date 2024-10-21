package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.Valuation
import com.systemsculpers.xbcad7319.view.adapter.PropertyAdapter
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter

class PropertyObserver (private val adapter: PropertyAdapter)
    : Observer<List<Property>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<Property>) {
        // initialize first element as the create button
        adapter.updateProperties(value)
    }
}