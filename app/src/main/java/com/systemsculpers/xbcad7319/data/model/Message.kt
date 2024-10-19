package com.systemsculpers.xbcad7319.data.model

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val timestamp: Long,
    val text: String
)
