package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Valuation
import com.systemsculpers.xbcad7319.view.adapter.UserValuationsAdapter
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter

class UserValuationsObserver (private val adapter: UserValuationsAdapter)
    : Observer<List<Valuation>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<Valuation>) {
        // initialize first element as the create button
        adapter.updateValuations(value)
    }
}