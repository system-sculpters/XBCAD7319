package com.systemsculpers.xbcad7319.view.custom

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Chat

class Dialogs {
    // This class is responsible for displaying various types of dialogs related to timeout events
    // and progress updates. It includes methods to show a timeout alert dialog, a progress dialog,
    // and a custom alert dialog for error messages. The dialogs help provide feedback to the user
    // about the application's state during network operations.

    fun showSuccessDialog(context: Context, errorMessage: String, onItemClick: (AlertDialog) -> Unit){
        // Inflate a custom layout for the error dialog.
        // This method was adapted from eeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_dialog, null)

        // Access the message and dismiss button from the dialog layout.
        val dialogMessage = dialogView.findViewById<TextView>(R.id.statusTextView)
        val dialogBtn = dialogView.findViewById<TextView>(R.id.dismissButton)

        // Set the error message text in the dialog.
        dialogMessage.text = errorMessage

        // Build the AlertDialog using the custom view.
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Set a click listener on the dismiss button to close the dialog.
        dialogBtn.setOnClickListener {
            onItemClick(dialogBuilder)
            dialogBuilder.dismiss() // Dismiss the dialog when the button is clicked.
        }

        // Show the alert dialog to the user.
        dialogBuilder.show()

        // Adjust the dialog's width and layout parameters programmatically.
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt() // Set width to 80% of screen width.
        val window = dialogBuilder.window // Get the dialog's window reference.

        // Set the layout parameters to position the dialog in the center.
        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        // Optionally, you can further customize the dialog's appearance (padding, margins, etc.).
        val layoutParams = window?.attributes
        layoutParams?.gravity = android.view.Gravity.CENTER // Center the dialog on the screen.
        window?.attributes = layoutParams // Apply the modified layout parameters to the window.
    }

    // Displays a custom alert dialog to inform the user about an error with a specified message.
    // The dialog can be dismissed by the user.
    fun showAlertDialog(context: Context, errorMessage: String) {
        // Inflate a custom layout for the error dialog.
        // This method was adapted from eeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val dialogView = LayoutInflater.from(context).inflate(R.layout.input_error_dialog, null)

        // Access the message and dismiss button from the dialog layout.
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val dialogBtn = dialogView.findViewById<TextView>(R.id.dismissButton)

        // Set the error message text in the dialog.
        dialogMessage.text = errorMessage

        // Build the AlertDialog using the custom view.
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Set a click listener on the dismiss button to close the dialog.
        dialogBtn.setOnClickListener {
            dialogBuilder.dismiss() // Dismiss the dialog when the button is clicked.
        }

        // Show the alert dialog to the user.
        dialogBuilder.show()

        // Adjust the dialog's width and layout parameters programmatically.
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt() // Set width to 80% of screen width.
        val window = dialogBuilder.window // Get the dialog's window reference.

        // Set the layout parameters to position the dialog in the center.
        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        // Optionally, you can further customize the dialog's appearance (padding, margins, etc.).
        val layoutParams = window?.attributes
        layoutParams?.gravity = android.view.Gravity.CENTER // Center the dialog on the screen.
        window?.attributes = layoutParams // Apply the modified layout parameters to the window.
    }

}