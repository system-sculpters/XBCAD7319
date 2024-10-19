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
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyDetailsBinding
import com.systemsculpers.xbcad7319.databinding.FragmentPropertyListingsBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyDetailsAdapter
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeFilterAdapter

class PropertyDetails : Fragment() {
//    val propertyTypes: MutableList<PropertyType> = mutableListOf(
//        PropertyType(, R.drawable.baseline_home_filled_24),
//        PropertyType(context.getString(R.string.rental), R.drawable.rental_icon),
//        PropertyType(context.getString(R.string.land), R.drawable.land_icon)
//    )

    // View binding object for accessing views in the layout
    private var _binding: FragmentPropertyDetailsBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!


    // Adapter for the RecyclerView to display goals
    private lateinit var propertyDetailsAdapter: PropertyDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)

        propertyDetailsAdapter = PropertyDetailsAdapter()


        // Find the message button (LinearLayout in your case)
        binding.contactAgent.setOnClickListener {
            showMessageDialog()
        }

        binding.seeOnMaps.setOnClickListener{

        }

        return binding.root
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



