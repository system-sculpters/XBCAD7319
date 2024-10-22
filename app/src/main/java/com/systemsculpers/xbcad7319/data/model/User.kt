package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: String = "",               // Unique identifier for the user
    val fullName: String = "",         // User's full name
    val email: String = "",            // User's email address
    val role: String = "",             // User role (e.g., 'agent', 'client')
    val phoneNumber: String = "",
    var token: String = "",            // Authentication token used for API requests and sessions
    var balance: Double = 0.0,         // The current balance associated with the user's account
    var password: String = ""           // The user's password (ideally stored securely, hashed)
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        fullName = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        role = parcel.readString() ?: "",
        phoneNumber = parcel.readString() ?: "",
        token = parcel.readString() ?: "",
        balance = parcel.readDouble(),
        password = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(role)
        parcel.writeString(phoneNumber)
        parcel.writeString(token)
        parcel.writeDouble(balance)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}