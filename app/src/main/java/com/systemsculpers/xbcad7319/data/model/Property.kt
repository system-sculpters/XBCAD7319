package com.systemsculpers.xbcad7319.data.model

data class Property(
    val id: String,                   // Unique identifier for the property
    val propertyType: String,         // Type of property (e.g., 'house', 'apartment')
    val location: Location,           // Location object containing address, latitude, and longitude
    val price: Double,                // Price of the property
    val description: String,          // Description of the property
    val createdAt: Long? = null,      // Timestamp of property creation
    val imageUrl: String? = null      // Optional URL for the property image
)
