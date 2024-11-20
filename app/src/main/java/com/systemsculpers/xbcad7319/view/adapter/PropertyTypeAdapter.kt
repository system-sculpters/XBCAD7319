package com.systemsculpers.xbcad7319.view.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.util.TypedValue
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
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeFilterAdapter.PropertyTypeViewHolder

class PropertyTypeAdapter(private val context: Context,
                          private val onItemClick: (PropertyType) -> Unit) :
    RecyclerView.Adapter<PropertyTypeAdapter.PropertyTypeViewHolder>() {

    private var selectedItemPosition = RecyclerView.NO_POSITION
    private var selectedPropertyType: PropertyType? = null

    val propertyTypes: MutableList<PropertyType> = mutableListOf(
        PropertyType(context.getString(R.string.house), R.drawable.baseline_home_filled_24),
        PropertyType(context.getString(R.string.rental), R.drawable.rental_icon),
        PropertyType(context.getString(R.string.land), R.drawable.land_icon)
    )



    // ViewHolder class for holding the views for each icon item
    inner class PropertyTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val propertyTypeImageView: ImageView = itemView.findViewById(R.id.property_type_icon)
        // TextView for displaying the icon name
        val propertyTypeNameText: TextView = itemView.findViewById(R.id.property_type_name)
        // ConstraintLayout for setting the background of the icon item
        val propertyTypeContainer: LinearLayout = itemView.findViewById(R.id.property_type_container)


        // Initialize click listener for the item view
        init {
            itemView.setOnClickListener {
                val position = adapterPosition // Get the position of the clicked item
                if (position != RecyclerView.NO_POSITION) {
                    val previousSelectedItem = selectedItemPosition
                    // Update the selected item position and trigger the onItemClick callback
                    selectedItemPosition = position
                    // Notify changes for previously and currently selected items
                    notifyItemChanged(previousSelectedItem)
                    notifyItemChanged(selectedItemPosition)

                    onItemClick(propertyTypes[position]) // Invoke the callback with the clicked category
                }
            }
        }
    }

    // Function to get the currently selected item's category
    fun getSelectedItem(): PropertyType? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            propertyTypes[selectedItemPosition] // Return the selected category
        } else {
            null // Return null if no item is selected
        }
    }

    // Function to set the selected category
    fun setSelectedItem(propertyType: PropertyType) {
        // Keep track of the previous selected position
        val previousSelectedPosition = selectedItemPosition
        selectedPropertyType = propertyType // Set the new selected category

        // Notify adapter of item change at the previous selected position
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }

        // Update selected position based on the new category
        selectedPropertyType?.let { propertyTypeVal ->
            Log.d("category", "this is the category: $propertyTypeVal")
            Log.d("category dataList", "this is the dataList $propertyTypes")
            val newSelectedPosition = propertyTypes.indexOf(propertyTypeVal) // Get the index of the new selected category
            if (newSelectedPosition != -1) {
                selectedItemPosition = newSelectedPosition // Update the selected item position
                // Notify adapter of item change at the new selected position
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyTypeViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_type_item_layout, parent, false)
        return PropertyTypeViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: PropertyTypeViewHolder, position: Int) {
        val currentItem = propertyTypes[position] // Get the current category item


        // Set the name of the category in the TextView
        holder.propertyTypeNameText.text = currentItem.name
        holder.propertyTypeImageView.setImageResource(currentItem.icon)

        if (position == selectedItemPosition) {
            holder.propertyTypeContainer.setBackgroundResource(R.drawable.selected_property_type_bg)

            holder.propertyTypeNameText.setTextColor( getTextColor(holder, R.attr.colorItemLayoutBg))

            holder.propertyTypeImageView.setColorFilter(getTextColor(holder, R.attr.colorItemLayoutBg))


        } else {
            // holder.propertyTypeContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.propertyTypeContainer.setBackgroundResource(R.drawable.edittext_background)

            holder.propertyTypeNameText.setTextColor(getTextColor(holder, R.attr.ItemBorder))

            holder.propertyTypeImageView.setColorFilter(getTextColor(holder, R.attr.ItemBorder), PorterDuff.Mode.SRC_IN)

        }
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return propertyTypes.size // Return the size of the categories list
    }

    // Fetches the theme-based text color for uncategorized transactions
    private fun getTextColor(holder: PropertyTypeViewHolder, attr: Int): Int {
        val typedValue = TypedValue()
        val theme = holder.itemView.context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
}