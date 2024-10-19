package com.systemsculpers.xbcad7319.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.data.model.Valuation

class ValuationsAdapter(private val onItemClick: (Valuation) -> Unit):
    RecyclerView.Adapter<ValuationsAdapter.ValuationViewHolder>() {

    // Mutable list to hold the transactions
    private val valuation = mutableListOf<Valuation>()

    // Function to update the adapter's data with a new list of transactions
    fun updateValuations(data: List<Valuation>) {
        valuation.clear() // Clear the existing transactions
        valuation.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }



    // ViewHolder class for holding the views for each icon item
    inner class ValuationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val fullName: TextView = itemView.findViewById(R.id.fullName)
        // TextView for displaying the icon name
        val email: TextView = itemView.findViewById(R.id.email)
        // ConstraintLayout for setting the background of the icon item
        val status: TextView = itemView.findViewById(R.id.status)
    }



    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValuationViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.valuation_item_layout, parent, false)
        return ValuationViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ValuationViewHolder, position: Int) {
        val currentItem = valuation[position] // Get the current category item


        // Set the name of the category in the TextView
        holder.status.text = currentItem.status

        holder.fullName.text = currentItem.user.fullName

        holder.email.text = currentItem.user.email

        val statusColor = if (currentItem.status == "pending") {
            ContextCompat.getColor(holder.itemView.context, R.color.yellow)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.green)
        }

        holder.status.setTextColor(statusColor)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return valuation.size // Return the size of the categories list
    }
}