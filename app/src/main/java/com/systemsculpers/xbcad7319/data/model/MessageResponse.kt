package com.systemsculpers.xbcad7319.data.model

data class MessageResponse(
    val messageId: String,
    val messages: List<Message> // Assuming Message is the model for individual messages
)
