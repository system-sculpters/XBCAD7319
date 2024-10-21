package com.systemsculpers.xbcad7319.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.LocationResult
import com.systemsculpers.xbcad7319.data.model.Valuation

class LocationAdapter (private val onItemClick: (LocationResult) -> Unit):
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    // Mutable list to hold the transactions
    private val valuation = mutableListOf<LocationResult>()

    // Function to update the adapter's data with a new list of transactions
    fun updateLocation(data: List<LocationResult>) {
        valuation.clear() // Clear the existing transactions
        valuation.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }


    // ViewHolder class for holding the views for each icon item
    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val address: TextView = itemView.findViewById(R.id.address)

    }


    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item_layout, parent, false)
        return LocationViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currentItem = valuation[position] // Get the current category item


        // Set the name of the category in the TextView
        holder.address.text = currentItem.display_name

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return valuation.size // Return the size of the categories list
    }
}
