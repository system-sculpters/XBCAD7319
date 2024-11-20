package com.systemsculpers.xbcad7319.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.systemsculpers.xbcad7319.data.model.User

class UserManager private constructor(context: Context) {
    // SharedPreferences instance to store user data
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Save user data to shared preferences

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun saveUser(user: User) {
        // Create an editor to modify shared preferences
        val editor = sharedPreferences.edit()

        // Store user ID, username, and email
        editor.putString("userid", user.id)
        editor.putString("role", user.role)
        editor.putString("email", user.email)
        editor.putString("fullName", user.fullName)
        editor.putString("phoneNumber", user.phoneNumber)

        // Apply changes asynchronously
        editor.apply()
    }

    // Retrieve user data from shared preferences

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun getUser(): User {
        // Get user details from shared preferences
        val userid = sharedPreferences.getString("userid", null)
        val email = sharedPreferences.getString("email", null)
        val role = sharedPreferences.getString("role", null)
        val fullName = sharedPreferences.getString("fullName", null)
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)


        // Create a User object; if any value is missing, return an empty User
        var user: User = User()
        if (userid != null && email != null && role != null && fullName != null && phoneNumber != null) {
            user = User(id = userid, email = email, role = role, fullName = fullName, phoneNumber = phoneNumber)
        }
        return user
    }

    // Clear user data from shared preferences (e.g., on logout)

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun clearUser() {
        // Create an editor to modify shared preferences
        val editor = sharedPreferences.edit()

        // Remove user ID, username, and email from shared preferences
        editor.remove("userid")
        editor.remove("role")
        editor.remove("email")
        editor.remove("fullName")
        editor.remove("phoneNumber")

        // Apply changes asynchronously
        editor.apply()
    }

    // Save the user's password (e.g., hashed password)

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun savePassword(password: String) {
        // Create an editor to modify shared preferences
        val editor = sharedPreferences.edit()

        // Store the password
        editor.putString("password", password)

        // Apply changes asynchronously
        editor.apply()
    }

    // Retrieve the user's password from shared preferences

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun getPassword(): String {
        // Get the password from shared preferences, assume it exists
        val password = sharedPreferences.getString("password", null)!!
        return password
    }

    // Companion object for singleton instance management
    companion object {
        // This object was adapted from stackoverflow
        // https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
        // aminography
        // https://stackoverflow.com/users/1631967/aminography
        @Volatile
        private var INSTANCE: UserManager? = null

        // Get the singleton instance of UserManager
        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserManager(context).also { INSTANCE = it }
            }
        }
    }
}
