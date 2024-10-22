package com.systemsculpers.xbcad7319.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.PropertyCount
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.data.model.PropertyCounts
import com.systemsculpers.xbcad7319.view.adapter.PropertyAnalyticsAdapter

class AnalyticsFragment : Fragment() {

    private lateinit var availableRecyclerView: RecyclerView
    private lateinit var soldRecyclerView: RecyclerView
    private lateinit var propertyAdapter: PropertyAnalyticsAdapter
    private lateinit var availableList: List<PropertyCount>
    private lateinit var soldList: List<PropertyCount>
    private lateinit var usersValueTextView: TextView // TextView for user count

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_analytics, container, false)

        // Initialize RecyclerViews
        availableRecyclerView = view.findViewById(R.id.availablePropertiesRecyclerView)
        soldRecyclerView = view.findViewById(R.id.soldPropertiesRecyclerView)

        // Initialize property counts (replace with actual data)
        val availableCounts = PropertyCounts(10, 20, 30, 0, 0, 0)
        val soldCounts = PropertyCounts(0, 0, 100, 10, 30, 20)

        // Populate the lists with data
        availableList = listOf(
            PropertyCount("Available Land", availableCounts.availableLand),
            PropertyCount("Available Rental", availableCounts.availableRental),
            PropertyCount("Available Sale", availableCounts.availableSale)
        )

        soldList = listOf(
            PropertyCount("Sold Land", soldCounts.soldLand),
            PropertyCount("Sold Rental", soldCounts.soldRental),
            PropertyCount("Sold Sale", soldCounts.soldSale)
        )

        // Set up RecyclerViews with adapters
        propertyAdapter = PropertyAnalyticsAdapter(availableList)
        availableRecyclerView.layoutManager = LinearLayoutManager(context)
        availableRecyclerView.adapter = propertyAdapter

        // Separate adapter for sold properties
        val soldPropertyAdapter = PropertyAnalyticsAdapter(soldList)
        soldRecyclerView.layoutManager = LinearLayoutManager(context)
        soldRecyclerView.adapter = soldPropertyAdapter

        // Initialize the user count TextView
        usersValueTextView = view.findViewById(R.id.users_value)

        // Fetch and update the user count
        fetchUserCount()

        return view
    }

    private fun fetchUserCount() {
        val userCount = getUserCountFromDatabase() // Replace with actual call
        usersValueTextView.text = userCount.toString()
    }

    private fun getUserCountFromDatabase(): Int {
        // Simulate a call to the backend or database
        return 10000 // Replace with actual fetched value
    }
}
