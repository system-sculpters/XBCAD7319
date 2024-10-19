package com.systemsculpers.xbcad7319.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Chat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatAdapter (private val onItemClick: (Chat) -> Unit):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // Mutable list to hold the transactions
    private val chats = mutableListOf<Chat>()

    // Function to update the adapter's data with a new list of transactions
    fun updateChats(data: List<Chat>) {
        chats.clear() // Clear the existing transactions
        chats.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }


    // ViewHolder class for holding the views for each icon item
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val fullName: TextView = itemView.findViewById(R.id.fullName)
        // TextView for displaying the icon name
        val message: TextView = itemView.findViewById(R.id.message)
        // ConstraintLayout for setting the background of the icon item
        val time: TextView = itemView.findViewById(R.id.time)
    }



    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item_layout, parent, false)
        return ChatViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val currentItem = chats[position] // Get the current chat item

        // Set the message in the TextView
        holder.message.text = currentItem.messages[currentItem.messages.size - 1].text

        // Set the full name of the participant
        holder.fullName.text = currentItem.participantsDetails.fullName

        // Get the timestamp of the message
        val timestamp = currentItem.messages[currentItem.messages.size - 1].timestamp
        val messageDate = Date(timestamp)

        // Check if the message was sent today
        val today = Calendar.getInstance()
        val messageCalendar = Calendar.getInstance().apply { time = messageDate }

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format for time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Format for date

        // If the message was sent today, show the time, else show the date
        holder.time.text = if (today.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == messageCalendar.get(Calendar.DAY_OF_YEAR)) {
            timeFormat.format(messageDate) // Display time
        } else {
            dateFormat.format(messageDate) // Display date
        }

        // Set the click listener
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return chats.size // Return the size of the categories list
    }
}