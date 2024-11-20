package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Message(
    val id: String = "",
    val chatId: String  = "",
    val senderId: String  = "",
    val timestamp: Long = 0,
    val text: String  = "",
    val isDateSeparator: Boolean = false,
    val dateString: String? = null
): Parcelable {

    // Constructor for creating Message object from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(), // Read boolean as a byte
        parcel.readString() // Read nullable String
    )

    // Write the Message object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(chatId)
        parcel.writeString(senderId)
        parcel.writeLong(timestamp)
        parcel.writeString(text)
        parcel.writeByte(if (isDateSeparator) 1 else 0) // Write boolean as a byte
        parcel.writeString(dateString)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}
