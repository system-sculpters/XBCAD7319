package com.systemsculpers.xbcad7319.data.model

data class User(
    val id: String = "",               // Unique identifier for the user
    val fullName: String = "",         // User's full name
    val email: String = "",            // User's email address
    val role: String = "",             // User role (e.g., 'agent', 'client')
    var token: String = "",             // Authentication token used for API requests and sessions
    var balance: Double = 0.0,          // The current balance associated with the user's account
    var password: String = "",          // The user's password (ideally stored securely, hashed)

)
