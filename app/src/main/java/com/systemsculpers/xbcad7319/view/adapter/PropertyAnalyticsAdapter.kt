package com.systemsculpers.xbcad7319.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.PropertyCount
import com.systemsculpers.xbcad7319.data.model.PropertyType

class PropertyAnalyticsAdapter(private val propertyList: List<PropertyCount>) : RecyclerView.Adapter<PropertyAnalyticsAdapter.PropertyViewHolder>() {

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyType: TextView = itemView.findViewById(R.id.property_type)
        val propertyCount: TextView = itemView.findViewById(R.id.property_counts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]
        holder.propertyType.text = property.name
        holder.propertyCount.text = property.count.toString() // Use count property instead of icon
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }
}
