package com.systemsculpers.xbcad7319.data.model

data class Valuation(
    val id: String = "",               // Unique identifier for the valuation request
    val userId: String = "",           // ID of the user requesting the valuation
    val propertyType: String = "",     // Type of property being valued
    val location: String = "",         // Location of the property
    val price: Double = 0.0,            // Price of the property
    val description: String = "",      // Description of the property
    val agentId: String = "",          // ID of the assigned agent
    val status: String = "",           // Status of the valuation (e.g., 'pending', 'completed')
    val createdAt: Long? = null,   // Timestamp of valuation request creation
    val user: User = User()
)
