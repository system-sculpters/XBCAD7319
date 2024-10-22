package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Purchase(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val amount: Double = 0.0,
    val purchaseDate: String = "",
    val property: Property = Property()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        propertyId = parcel.readString() ?: "",
        amount = parcel.readDouble(),
        purchaseDate = parcel.readString() ?: "",
        property = parcel.readParcelable(Property::class.java.classLoader) ?: Property()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(propertyId)
        parcel.writeDouble(amount)
        parcel.writeString(purchaseDate)
        parcel.writeParcelable(property, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Purchase> {
        override fun createFromParcel(parcel: Parcel): Purchase {
            return Purchase(parcel)
        }

        override fun newArray(size: Int): Array<Purchase?> {
            return arrayOfNulls(size)
        }
    }
}
