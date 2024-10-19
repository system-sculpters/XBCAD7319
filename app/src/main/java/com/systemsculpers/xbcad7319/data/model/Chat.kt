package com.systemsculpers.xbcad7319.data.model

data class Chat(
    val id: String,                         // Unique identifier for the chat
    val participants: List<String>,         // List of user IDs participating in the chat
    val messages: List<Message>,            // List of messages in the chat
    val createdAt: Long? = null,            // Timestamp of chat creation
    val valuationId: String? = null,         // Link to associated valuation request, if any
    val participantsDetails: User
)
