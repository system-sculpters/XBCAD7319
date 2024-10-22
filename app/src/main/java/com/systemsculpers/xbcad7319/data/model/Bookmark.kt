package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Bookmark(
    val id: String = "",                // Unique identifier for the property
    val propertyId: String = "",
    val userId: String = "",
    val createdAt: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        propertyId = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        createdAt = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(propertyId)
        parcel.writeString(userId)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bookmark> {
        override fun createFromParcel(parcel: Parcel): Bookmark {
            return Bookmark(parcel)
        }

        override fun newArray(size: Int): Array<Bookmark?> {
            return arrayOfNulls(size)
        }
    }
}
