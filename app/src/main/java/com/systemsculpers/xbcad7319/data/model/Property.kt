package com.systemsculpers.xbcad7319.data.model

import android.os.Parcel
import android.os.Parcelable

data class Property(
    val id: String = "",                      // Unique identifier for the property
    val title: String = "",                   // Title of the property
    val propertyType: String = "",            // Type of property (e.g., 'house', 'apartment')
    var location: Location = Location(),              // Location object containing address, latitude, and longitude
    val price: Double = 0.0,                   // Price of the property
    val description: String = "",             // Description of the property
    val rooms:  Int = 0,                      // Number of rooms
    val bathrooms:  Int = 0,                  // Number of bathrooms
    val parking: Int = 0,                    // Number of parking spaces
    val size: Int = 0,                       // Size of the property in square meters
    val createdAt: Long? = null,         // Timestamp of property creation (nullable)
    val images: List<String>? = null,    // Optional list of image URLs
    var agentId: String = "",
    var ownerId: String = "",
    var status: String = "",
    var isBookmarked: Boolean = false,
    var user: User? = User()
): Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        propertyType = parcel.readString() ?: "",
        location = parcel.readParcelable(Location::class.java.classLoader) ?: Location("", 0.0, 0.0),
        price = parcel.readDouble(),
        description = parcel.readString() ?: "",
        rooms = parcel.readInt(),
        bathrooms = parcel.readInt(),
        parking = parcel.readInt(),
        size = parcel.readInt(),
        createdAt = parcel.readValue(Long::class.java.classLoader) as? Long,
        images = parcel.createStringArrayList(), // Creates a list of strings from the parcel
        agentId = parcel.readString() ?: "",
        ownerId = parcel.readString() ?: "",
        status = parcel.readString() ?: "",
        isBookmarked = parcel.readByte() != 0.toByte(),
        user = parcel.readParcelable(User::class.java.classLoader) ?: User()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(propertyType)
        parcel.writeParcelable(location, flags)
        parcel.writeDouble(price)
        parcel.writeString(description)
        parcel.writeInt(rooms)
        parcel.writeInt(bathrooms)
        parcel.writeInt(parking)
        parcel.writeInt(size)
        parcel.writeValue(createdAt)
        parcel.writeStringList(images) // Writes the list of strings to the parcel
        parcel.writeString(agentId)
        parcel.writeString(ownerId)
        parcel.writeString(status)
        parcel.writeByte(if (isBookmarked) 1 else 0)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Property> {
        override fun createFromParcel(parcel: Parcel): Property {
            return Property(parcel)
        }

        override fun newArray(size: Int): Array<Property?> {
            return arrayOfNulls(size)
        }
    }
}
