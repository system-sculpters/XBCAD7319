package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Address(
    val house_number: String?,
    val road: String?,
    val suburb: String?,
    val city: String?,
    val state: String?,
    val postcode: String?,
    val country: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        house_number = parcel.readString(),
        road = parcel.readString(),
        suburb = parcel.readString(),
        city = parcel.readString(),
        state = parcel.readString(),
        postcode = parcel.readString(),
        country = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(house_number)
        parcel.writeString(road)
        parcel.writeString(suburb)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(postcode)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}
