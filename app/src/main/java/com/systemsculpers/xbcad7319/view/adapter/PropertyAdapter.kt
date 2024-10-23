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
import com.bumptech.glide.Glide
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.LocationResult
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.view.custom.RoundedCornersTransformation

class PropertyAdapter (private val context: Context,
                       private val onItemClick: (Property) -> Unit) :
    RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {


    private val propertyList = mutableListOf<Property>()

    // Function to update the adapter's data with a new list of transactions
    fun updateProperties(data: List<Property>) {
        propertyList.clear() // Clear the existing transactions
        propertyList.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // ViewHolder class for holding the views for each icon item
    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyImage: ImageView = itemView.findViewById(R.id.property_image);
        val propertyType: TextView = itemView.findViewById(R.id.property_type);
        val propertyName: TextView  = itemView.findViewById(R.id.property_name);
        val propertyAddress: TextView  = itemView.findViewById(R.id.property_address);
        val noOfRooms: TextView  = itemView.findViewById(R.id.no_of_rooms);
        val noOfBathrooms: TextView  = itemView.findViewById(R.id.no_of_bathrooms);
        val noOfParking: TextView  = itemView.findViewById(R.id.no_of_parkings);
        val propertyPrice: TextView  = itemView.findViewById(R.id.property_price);
    }


    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_listing_item_layout, parent, false)
        return PropertyViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]
        holder.propertyType.text = property.propertyType
        holder.propertyName.text = property.title
        holder.propertyAddress.text = property.location.address
        holder.noOfRooms.text = property.rooms.toString()
        holder.noOfBathrooms.text = property.bathrooms.toString()
        holder.noOfParking.text = property.parking.toString()
        holder.propertyPrice.text = property.price.toString()

        // Load the image using Glide
        Glide.with(context)
            .load(property.images?.get(0)) // Use image URL or resource ID
            .placeholder(R.drawable.house_image) // Placeholder image
            .transform(RoundedCornersTransformation(17f)) // Apply rounded corners
            .into(holder.propertyImage)

        holder.itemView.setOnClickListener {
            onItemClick(property)
        }

        val propTypeColor = if(property.propertyType == "Land"){
            R.color.green
        } else if(property.propertyType == "House"){
            R.color.blue
        } else{
            R.color.red
        }

        val originalColor = ContextCompat.getColor(holder.itemView.context, propTypeColor) // Get the color

        holder.propertyType.setTextColor(originalColor)
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return propertyList.size // Return the size of the categories list
    }
}