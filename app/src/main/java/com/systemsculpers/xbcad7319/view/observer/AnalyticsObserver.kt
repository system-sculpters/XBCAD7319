package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.analytics.AnalyticsResponse
import com.systemsculpers.xbcad7319.view.adapter.AnalyticsAdapter
import com.systemsculpers.xbcad7319.view.adapter.GraphAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyAdapter
import com.systemsculpers.xbcad7319.view.fragment.AnalyticsFragment

class AnalyticsObserver (
    private val fragment: AnalyticsFragment
)
    : Observer<AnalyticsResponse> {

    // Method called when the observed data changes
    override fun onChanged(value: AnalyticsResponse) {
        // Filter the list to only include bookmarked properties

        // Update the adapter with the filtered list
        fragment.updateAnalytics(value)
    }
}