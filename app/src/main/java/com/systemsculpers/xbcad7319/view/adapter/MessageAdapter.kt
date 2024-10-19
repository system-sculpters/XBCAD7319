package com.systemsculpers.xbcad7319.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Message

class MessageAdapter (
    private val currentUserId: String // Pass the current user's ID
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages: MutableList<Message> = mutableListOf()

    fun updateMessages(data: List<Message>){
        messages.clear() // Clear the existing transactions
        messages.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == 0) { // 0 for outgoing messages
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_outgoing, parent, false)
            MessageViewHolder(view)
        } else { // 1 for incoming messages
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_incoming, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.textView.text = message.text
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            0 // Outgoing message
        } else {
            1 // Incoming message
        }
    }

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.messageTextView)
    }
}