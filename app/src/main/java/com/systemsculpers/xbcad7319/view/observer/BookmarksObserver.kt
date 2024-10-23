package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.view.adapter.PropertyAdapter
import com.systemsculpers.xbcad7319.view.fragment.PropertyListings

class BookmarksObserver(
    private val adapter: PropertyAdapter,
)
    : Observer<List<Property>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<Property>) {
        // Filter the list to only include bookmarked properties
        val bookmarkedProperties = value.filter { it.isBookmarked }

        // Update the adapter with the filtered list
        adapter.updateProperties(bookmarkedProperties)
    }
}