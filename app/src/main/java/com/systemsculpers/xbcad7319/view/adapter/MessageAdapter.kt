package com.systemsculpers.xbcad7319.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Message

class MessageAdapter(
    private val currentUserId: String // Pass the current user's ID
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages: MutableList<Message> = mutableListOf()

    fun updateMessages(data: List<Message>) {
        messages.clear()
        val sortedMessages = data.sortedBy { it.timestamp }

        var lastDate: String? = null
        for (message in sortedMessages) {
            val currentDate = formatDate(message.timestamp)
            if (currentDate != lastDate) {
                // Add a date separator
                messages.add(Message(isDateSeparator = true, dateString = currentDate))
                lastDate = currentDate
            }
            messages.add(message)
        }
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            0 -> { // Outgoing message
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_outgoing, parent, false)
                MessageViewHolder(view)
            }
            1 -> { // Incoming message
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_incoming, parent, false)
                MessageViewHolder(view)
            }
            else -> { // Date separator
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_separator, parent, false)
                MessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        if (message.isDateSeparator) {
            holder.dateTextView?.visibility = View.VISIBLE
            holder.dateTextView?.text = message.dateString
            holder.textView?.visibility = View.GONE
            holder.timeTextView?.visibility = View.GONE
        } else {
            holder.dateTextView?.visibility = View.GONE
            holder.textView?.visibility = View.VISIBLE
            holder.timeTextView?.visibility = View.VISIBLE

            holder.textView?.text = message.text
            holder.timeTextView?.text = formatTime(message.timestamp)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isDateSeparator -> 2 // Date separator
            messages[position].senderId == currentUserId -> 0 // Outgoing message
            else -> 1 // Incoming message
        }
    }

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView? = view.findViewById(R.id.messageTextView)
        val timeTextView: TextView? = view.findViewById(R.id.timeTextView)
        val dateTextView: TextView? = view.findViewById(R.id.dateTextView)
    }
}
