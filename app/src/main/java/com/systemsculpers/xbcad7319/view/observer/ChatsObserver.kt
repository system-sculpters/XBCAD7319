package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.Message
import com.systemsculpers.xbcad7319.data.model.Valuation
import com.systemsculpers.xbcad7319.view.adapter.ChatAdapter
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter

class ChatsObserver (private val adapter: ChatAdapter)
    : Observer<List<Chat>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<Chat>) {
        // Sort the chats by the most recent message timestamp (assuming Chat has a property `lastMessageTimestamp`)
        val sortedChats = value.sortedByDescending { chat -> chat.createdAt }.toMutableList()

        for (chat in sortedChats) {
            val sortedMessages = chat.messages.sortedBy { message: Message -> message.timestamp }

            val unsortedMessages = chat.messages.toMutableList()

            unsortedMessages.clear()

            unsortedMessages.addAll(sortedMessages)

            chat.messages = unsortedMessages
        }


        // Update the adapter with the sorted chats
        adapter.updateChats(sortedChats)
    }
}