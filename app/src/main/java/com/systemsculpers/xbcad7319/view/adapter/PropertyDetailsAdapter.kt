package com.systemsculpers.xbcad7319.view.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.PropertyType

class PropertyDetailsAdapter:
    RecyclerView.Adapter<PropertyDetailsAdapter.PropertyTypeViewHolder>() {

    // Mutable list to hold the transactions
    private val propertyTypes = mutableListOf<PropertyType>()

    // Function to update the adapter's data with a new list of transactions
    fun updatePropertyTypes(data: List<PropertyType>) {
        propertyTypes.clear() // Clear the existing transactions
        propertyTypes.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }



    // ViewHolder class for holding the views for each icon item
    inner class PropertyTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val propertyTypeImageView: ImageView = itemView.findViewById(R.id.property_type_icon)
        // TextView for displaying the icon name
        val propertyTypeNameText: TextView = itemView.findViewById(R.id.property_type_name)
        // ConstraintLayout for setting the background of the icon item
    }



    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyTypeViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_details_item_layout, parent, false)
        return PropertyTypeViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: PropertyTypeViewHolder, position: Int) {
        val currentItem = propertyTypes[position] // Get the current category item


        // Set the name of the category in the TextView
        holder.propertyTypeNameText.text = currentItem.name
        holder.propertyTypeImageView.setImageResource(currentItem.icon)
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return propertyTypes.size // Return the size of the categories list
    }
}