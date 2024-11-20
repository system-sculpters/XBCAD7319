package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Chat(
    val id: String,                         // Unique identifier for the chat
    val participants: List<String>,         // List of user IDs participating in the chat
    var messages: List<Message>,            // List of messages in the chat
    val createdAt: Long? = null,            // Timestamp of chat creation
    val valuationId: String? = null,         // Link to associated valuation request, if any
    val participantsDetails: User
)  : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        participants = parcel.createStringArrayList() ?: emptyList(),
        messages = mutableListOf<Message>().apply {
            parcel.readList(this, Message::class.java.classLoader)
        },
        createdAt = parcel.readValue(Long::class.java.classLoader) as? Long,
        valuationId = parcel.readString(),
        participantsDetails = parcel.readParcelable(User::class.java.classLoader) ?: User()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeStringList(participants)
        parcel.writeList(messages)
        parcel.writeValue(createdAt)
        parcel.writeString(valuationId)
        parcel.writeParcelable(participantsDetails, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}
