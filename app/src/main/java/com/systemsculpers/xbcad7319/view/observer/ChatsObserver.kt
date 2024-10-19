package com.systemsculpers.xbcad7319.view.observer

import androidx.lifecycle.Observer
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.Valuation
import com.systemsculpers.xbcad7319.view.adapter.ChatAdapter
import com.systemsculpers.xbcad7319.view.adapter.ValuationsAdapter

class ChatsObserver (private val adapter: ChatAdapter)
    : Observer<List<Chat>> {

    // Method called when the observed data changes
    override fun onChanged(value: List<Chat>) {
        // Sort the chats by the most recent message timestamp (assuming Chat has a property `lastMessageTimestamp`)
        val sortedChats = value.sortedByDescending { chat -> chat.createdAt }

        // Update the adapter with the sorted chats
        adapter.updateChats(sortedChats)
    }
}