package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable


data class LocationResult(
    val display_name: String,
    val lat: String,
    val lon: String,
    val address: Address?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        display_name = parcel.readString() ?: "",
        lat = parcel.readString() ?: "",
        lon = parcel.readString() ?: "",
        address = parcel.readParcelable(Address::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(display_name)
        parcel.writeString(lat)
        parcel.writeString(lon)
        parcel.writeParcelable(address, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationResult> {
        override fun createFromParcel(parcel: Parcel): LocationResult {
            return LocationResult(parcel)
        }

        override fun newArray(size: Int): Array<LocationResult?> {
            return arrayOfNulls(size)
        }
    }
}
