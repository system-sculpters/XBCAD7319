package com.systemsculpers.xbcad7319.data.model

data class SendMessage(
    val senderId: String,
    val chatId: String,                   // ID of the message sender
    val text: String,
)
