package com.systemsculpers.xbcad7319.view.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.AnalyticsValue
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.analytics.AnalyticsResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsAdapter (private val onItemClick: (AnalyticsValue) -> Unit):
    RecyclerView.Adapter<AnalyticsAdapter.AnalyticsViewHolder>() {

    // Mutable list to hold the transactions
    private val values = mutableListOf<AnalyticsValue>()

    //private val analyticsResponse = AnalyticsResponse()

    // Function to update the adapter's data with a new list of transactions
    fun updateAnalytics(data: List<AnalyticsValue>) {
        values.clear() // Clear the existing transactions
        values.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Function to update the adapter's data with a new list of transactions
//    fun updateAnalyticsResponse(data: AnalyticsResponse) {
//        values.clear() // Clear the existing transactions
//        values.addAll(data) // Add new transactions to the list
//        notifyDataSetChanged() // Notify the adapter that the data has changed
//    }


    // ViewHolder class for holding the views for each icon item
    inner class AnalyticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val value: TextView = itemView.findViewById(R.id.value)
        // TextView for displaying the icon name
        val title: TextView = itemView.findViewById(R.id.title)

        val icon: ImageView = itemView.findViewById(R.id.icon)

        val container: LinearLayout = itemView.findViewById(R.id.container)
    }



    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalyticsViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.analytics_item_layout, parent, false)
        return AnalyticsViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: AnalyticsViewHolder, position: Int) {
        val currentItem = values[position] // Get the current analytics item

        // Set the value and title in the TextViews
        holder.value.text = currentItem.value
        holder.title.text = currentItem.title
        holder.icon.setImageResource(currentItem.icon)

        // Get the original color for the border
        val borderColor = ContextCompat.getColor(holder.itemView.context, currentItem.color) // Border color
        var crnerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius) // Corner radius

        // Create a GradientDrawable for the border
        val borderDrawable = GradientDrawable().apply {
            setColor(Color.TRANSPARENT) // Set the background color to transparent
            setStroke(holder.itemView.context.resources.getDimensionPixelSize(R.dimen.border_width), borderColor) // Set the border width and color
            cornerRadius = crnerRadius.toFloat() // Set corner radius for the border
            // Optional: You can also set individual corner radii if needed
            // setCornerRadii(floatArrayOf(cornerRadius.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat()))
        }

        holder.container.background = borderDrawable // Apply the border drawable as background

        // Set the click listener
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return values.size // Return the size of the categories list
    }
}