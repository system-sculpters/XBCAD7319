package com.systemsculpers.xbcad7319

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppConstants {
    // The base URL for the API endpoints used throughout the application.
    // This URL is used as the prefix for all network requests.


    const val BASE_URL = "https://xbcad7319-api.onrender.com/api/"

    //const val BASE_URL = "http://10.0.2.2:3001/api/"

    // Converts a timestamp (in milliseconds) to a formatted date string (dd/MM/yyyy).
    // This function is useful for displaying dates in a user-friendly format.
    fun convertLongToString(timestamp: Long): String {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel
        val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = Date(timestamp)
        val formattedDate: String = sdf.format(date)
        return formattedDate
    }

    // Checks whether the token has expired based on the provided expiration time.
    // Returns true if the current system time exceeds the expiration time, indicating
    // that the token is no longer valid for authentication.
    fun isTokenExpired(expirationTime: Long): Boolean {
        return System.currentTimeMillis() > expirationTime
    }

    // Calculates the expiration time for a token, which is set to two days from the current time.
    // This function can be used to set a validity period for authentication tokens.
    fun tokenExpirationTime(): Long {
        return System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000)
    }

    // Formats a given amount as a string with two decimal places.
    // This function is useful for displaying monetary values in a consistent format throughout the app.
    fun formatAmount(amount: Double): String {
        return String.format(Locale.US, "%.2f", amount)
    }

    // Converts a timestamp (in milliseconds) to a formatted date string (dd/MM/yyyy).
    // This function is similar to convertLongToString and can be used for date formatting tasks.
    fun longToDate(timestamp: Long): String {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = sdf.format(date)
        return formattedDate
    }

    // Converts a date string in "dd/MM/yyyy" format to a Long timestamp
    fun convertStringToLong(dateString: String): Long {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel

        // Create a date format object for parsing the date string
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the date string into a Date object
        val date: Date = dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")

        // Return the timestamp of the date
        return date.time
    }
}