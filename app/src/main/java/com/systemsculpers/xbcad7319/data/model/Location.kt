package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Location(
    var address: String="",           // Address of the property
    var latitude: Double = 0.0,          // Latitude of the property location
    var longitude: Double = 0.0          // Longitude of the property location
) : Parcelable {
    constructor(parcel: Parcel) : this(
        address = parcel.readString() ?: "",
        latitude = parcel.readDouble(),
        longitude = parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}
