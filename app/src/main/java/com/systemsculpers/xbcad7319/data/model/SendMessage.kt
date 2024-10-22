package com.systemsculpers.xbcad7319.data.model

data class SendMessage(
    val userId: String = "",
    val agentId: String = "",
    val senderId: String = "",
    val chatId: String = "",                   // ID of the message sender
    val text: String = "",
)
