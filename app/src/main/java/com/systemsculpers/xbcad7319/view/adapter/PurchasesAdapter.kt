package com.systemsculpers.xbcad7319.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.systemsculpers.xbcad7319.AppConstants
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.Purchase
import com.systemsculpers.xbcad7319.view.custom.RoundedCornersTransformation

class PurchasesAdapter (private val context: Context,
                        private val onItemClick: (Property) -> Unit) :
    RecyclerView.Adapter<PurchasesAdapter.PurchaseViewHolder>() {


    private val purchaseList = mutableListOf<Purchase>()

    // Function to update the adapter's data with a new list of transactions
    fun updatePurchases(data: List<Purchase>) {
        purchaseList.clear() // Clear the existing transactions
        purchaseList.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // ViewHolder class for holding the views for each icon item
    inner class PurchaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyImage: ImageView = itemView.findViewById(R.id.property_image);
        val propertyType: TextView = itemView.findViewById(R.id.property_type);
        val propertyName: TextView = itemView.findViewById(R.id.property_name);
        val propertyAddress: TextView = itemView.findViewById(R.id.property_address);
        val noOfRooms: TextView = itemView.findViewById(R.id.no_of_rooms);
        val noOfBathrooms: TextView = itemView.findViewById(R.id.no_of_bathrooms);
        val noOfParking: TextView = itemView.findViewById(R.id.no_of_parkings);
        val propertyPrice: TextView = itemView.findViewById(R.id.property_price);
        val isSoldImage: ImageView = itemView.findViewById(R.id.is_sold_image)
        val isSold: TextView = itemView.findViewById(R.id.isSold)
    }


    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_listing_item_layout, parent, false)
        return PurchaseViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        val property = purchaseList[position].property
        holder.propertyType.text = property.propertyType
        holder.propertyName.text = property.title
        holder.propertyAddress.text = property.location.address
        holder.noOfRooms.text = property.rooms.toString()
        holder.noOfBathrooms.text = property.bathrooms.toString()
        holder.noOfParking.text = property.parking.toString()
        holder.propertyPrice.text = "R${AppConstants.formatAmount(property.price)}"
        // Load the image using Glide
        Glide.with(context)
            .load(property.images?.get(0)) // Use image URL or resource ID
            .placeholder(R.drawable.image_search) // Placeholder image
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

        val isSoldImg = if(property.status == "Sold"){
            R.drawable.sold
        } else {
            R.drawable.for_sale
        }

        holder.isSoldImage.setImageResource(isSoldImg)

        holder.isSold.text = property.status

    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return purchaseList.size // Return the size of the categories list
    }
}