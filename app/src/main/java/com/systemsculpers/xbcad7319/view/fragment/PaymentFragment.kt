package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.PurchaseController
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.Purchase
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentPaymentBinding
import com.systemsculpers.xbcad7319.view.custom.Dialogs


class PaymentFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentPaymentBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    private var property: Property? = Property()

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private lateinit var purchaseController: PurchaseController

    // Variable to store the error message if input validation fails
    private var errorMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            property = it.getParcelable(PROPERTY_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)

        purchaseController = ViewModelProvider(this).get(PurchaseController::class.java)

        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        binding.propertyPrice.text = property?.price.toString()

        binding.buyNow.setOnClickListener {
            pay()
        }

        binding.expiryDate.filters = arrayOf(InputFilter.LengthFilter(5)) // Max 5 characters
        binding.expiryDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.length == 2 && !input.contains("/")) {
                    binding.expiryDate.setText("$input/")
                    binding.expiryDate.setSelection(binding.expiryDate.text?.length!!)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.payment))
    }

    // Function to set up user details and observe the view model for transaction updates
    private fun pay() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }

    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        Log.d("token", "token: ${token}")
        val nameOnCard = binding.nameOnCard.text.toString()
        val cardNo = binding.cardNo.text.toString()
        val cvv = binding.cvv.text.toString()
        val expiryDate = binding.expiryDate.text.toString()

        val prop = property

        // Validate user input before sending data to the server
        if (!validatePayment(prop, nameOnCard, cardNo, cvv, expiryDate)) {
            Dialogs().showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        val newPayment = prop?.let {
            Purchase(
                userId = userId, propertyId = it.id, amount = it.price)
        }
        // Observe the status of the transaction fetching operation
        purchaseController.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                Dialogs().showSuccessDialog(requireContext(), getString(R.string.successful_purchase)){
                    // change fragment
                    changeCurrentFragment(PurchasesFragment())

                    it.dismiss()
                }
                Log.d("status", "successful")

            } else {
                Log.d("status", "fail")
            }
        }

        // Observe any messages from the ViewModel
        purchaseController.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Log the message for debugging purposes
            Log.d("Valuations message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                Log.d("failed retrieval", "Retry...")

                purchaseController.createPurchase(token, newPayment!!)

            }
        }

        // Initial call to fetch all transactions for the user
        purchaseController.createPurchase(token, newPayment!!)
    }

    private fun validatePayment(prop: Property?, nameOnCard: String, cardNo: String, cvv: String, expiryDate: String): Boolean {
        var errors = 0

        // Check if property is valid
        if (prop == null) {
            errors += 1
            errorMessage += "${getString(R.string.enter_valid_property)}\n"
        }

        // Check if name on the card is entered
        if (nameOnCard.isEmpty()) {
            errors += 1
            errorMessage += "${getString(R.string.enter_name_on_card)}\n"
        }

        // Check if card number is entered and is 16 digits
        val cardNoPattern = Regex("^\\d{16}$")
        if (cardNo.isEmpty()) {
            errors += 1
            errorMessage += "${getString(R.string.enter_card_number)}\n"
        } else if (!cardNoPattern.matches(cardNo)) {
            errors += 1
            errorMessage += "${getString(R.string.invalid_card_number)}\n"
        }

        // Check if CVV is entered and is 3 digits
        val cvvPattern = Regex("^\\d{3}$")
        if (cvv.isEmpty()) {
            errors += 1
            errorMessage += "${getString(R.string.enter_cvv)}\n"
        } else if (!cvvPattern.matches(cvv)) {
            errors += 1
            errorMessage += "${getString(R.string.invalid_cvv)}\n"
        }

        // Check if expiry date is in MM/YY format
        val expiryDatePattern = Regex("^(0[1-9]|1[0-2])/\\d{2}$")
        if (expiryDate.isEmpty()) {
            errors += 1
            errorMessage += "${getString(R.string.enter_expiry_date)}\n"
        } else if (!expiryDatePattern.matches(expiryDate)) {
            errors += 1
            errorMessage += "${getString(R.string.invalid_expiry_date_format)}\n"
        }

        return errors == 0
    }

    // Helper function to change the current fragment in the activity.
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val PROPERTY_ARG = "property"
        @JvmStatic
        fun newInstance(property: Property) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PROPERTY_ARG, property)
                }
            }
    }
}