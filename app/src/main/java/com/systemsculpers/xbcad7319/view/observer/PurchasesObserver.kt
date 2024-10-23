package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Purchase
import com.systemsculpers.xbcad7319.view.adapter.PurchasesAdapter

class PurchasesObserver (private val adapter: PurchasesAdapter)
    : Observer<List<Purchase>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<Purchase>) {
        // initialize first element as the create button
        adapter.updatePurchases(value)
    }
}