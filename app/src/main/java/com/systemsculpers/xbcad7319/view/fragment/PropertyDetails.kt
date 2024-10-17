package com.systemsculpers.xbcad7319.view.fragment

import android.app.AlertDialog
import com.systemsculpers.xbcad7319.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment

class PropertyDetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_property_details, container, false)

        // Find the message button (LinearLayout in your case)
        val messageButton: View = view.findViewById(R.id.message_container)

        // Set an OnClickListener for the message button
        messageButton.setOnClickListener {
            showMessageDialog()
        }

        return view
    }
    companion object {
        fun newInstance(param1: String, param2: String) =
            PropertyDetails().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }

    private fun showMessageDialog() {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_message, null)

        // Initialize dialog elements
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
        val etMobile = dialogView.findViewById<EditText>(R.id.etMobile)
        val etMessage = dialogView.findViewById<EditText>(R.id.etMessage)
        val btnSend = dialogView.findViewById<Button>(R.id.btnSend)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClose) // Add this line to find the close button

        // Build the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Close button action
        btnClose.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when close button is clicked
        }

        // Send button action
        btnSend.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mobile = etMobile.text.toString()
            val message = etMessage.text.toString()

            // Handle the form data here (e.g., send the data to the server or save it)
            // For now, just dismiss the dialog
            dialog.dismiss()

            // You can also add your validation logic here
        }

        // Show the dialog
        dialog.show()
    }

}



