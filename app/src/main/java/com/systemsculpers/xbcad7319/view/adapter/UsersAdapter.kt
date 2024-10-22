package com.systemsculpers.xbcad7319.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.User
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UsersAdapter (private val onItemClick: (User) -> Unit):
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    // Mutable list to hold the transactions
    private val users = mutableListOf<User>()

    // Function to update the adapter's data with a new list of transactions
    fun updateUsers(data: List<User>) {
        users.clear() // Clear the existing transactions
        users.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }


    // ViewHolder class for holding the views for each icon item
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val fullName: TextView = itemView.findViewById(R.id.fullName)
        // TextView for displaying the icon name
        val email: TextView = itemView.findViewById(R.id.email)
    }



    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = users[position] // Get the current chat item

        // Set the message in the TextView
        holder.email.text = currentItem.email

        // Set the full name of the participant
        holder.fullName.text = currentItem.fullName



        // Set the click listener
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return users.size // Return the size of the categories list
    }
}