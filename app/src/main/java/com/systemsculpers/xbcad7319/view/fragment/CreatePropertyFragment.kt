package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.api.controller.PropertyController
import com.systemsculpers.xbcad7319.data.model.Location
import com.systemsculpers.xbcad7319.data.model.LocationResult
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.data.model.PropertyType
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.data.preferences.TokenManager
import com.systemsculpers.xbcad7319.data.preferences.UserManager
import com.systemsculpers.xbcad7319.databinding.FragmentCreatePropertyBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CreatePropertyFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentCreatePropertyBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyTypeAdapter: PropertyTypeAdapter

    private lateinit var controller: PropertyController

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private var propType: String = ""

    // Variable to store the error message if input validation fails
    private var errorMessage = ""
    private lateinit var dialog: Dialogs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentCreatePropertyBinding.inflate(inflater, container, false)

        controller = ViewModelProvider(this).get(PropertyController::class.java)
        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        dialog = Dialogs()

        setPropertyTypes()

        val selectedUser = arguments?.getParcelable<User>("user")
        if (selectedUser != null) {
            // Update UI (e.g., show the number of images selected)
            binding.owner.text = selectedUser.fullName
        }
        // Check if images are passed back
        val selectedImages = arguments?.getParcelableArrayList<Uri>("selected_images")
        if (selectedImages != null) {
            // Update UI (e.g., show the number of images selected)
            binding.imageNumber.text = "${selectedImages.size} images selected"
        }

        val location =  arguments?.getParcelable<LocationResult>("location")

        if(location != null){
            binding.location.text = location.display_name
        }

        val property =  arguments?.getParcelable<Property>("property")

        if(property != null){
            binding.propertyName.setText(property.title)
            binding.propertyPrice.setText(property.price.toString())
            binding.propertyDescription.setText(property.description)
            binding.noOfBedrooms.setText(property.rooms.toString())
            binding.noOfBathrooms.setText(property.bathrooms.toString())
            binding.noOfParking.setText(property.parking.toString())
            binding.size.setText(property.size.toString())

            val propertyTypes: MutableList<PropertyType> = mutableListOf(
                PropertyType(getString(R.string.house), R.drawable.baseline_home_filled_24),
                PropertyType(getString(R.string.rental), R.drawable.rental_icon),
                PropertyType(getString(R.string.land), R.drawable.land_icon)
            )

            val selectedTypeIndex = propertyTypes.indexOfFirst { it.name == property.propertyType }
            if (selectedTypeIndex != -1) {
                val pType =propertyTypes[selectedTypeIndex]
                propertyTypeAdapter.setSelectedItem(pType)  // Pass the selected index to the adapter
                propType = pType.name
                updatePropertyDetailsUi(propType)

            }
        }

        binding.ownerRedirect.setOnClickListener {
            redirectSelectUser()
        }

        binding.locationRedirect.setOnClickListener{
            redirectToLocation()
        }

        binding.uploadImageRedirect.setOnClickListener{
            redirectUpload()
        }

        binding.submitButton.setOnClickListener {
            createProperty()
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.create_property))
    }

    // Sets up the color picker RecyclerView
    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 3) // 3 icons per row
        //binding.properyTypesList.setHasFixedSize(false)

        // Adapter to display available colors and handle color selection
        propertyTypeAdapter = PropertyTypeAdapter(requireContext()) { selectedPropertyType ->
            Log.d("SelectedCategory", "Selected Property Type: $selectedPropertyType")
            propType = selectedPropertyType.name

            updatePropertyDetailsUi(propType)
        }

        binding.propertyTypesList.adapter = propertyTypeAdapter
    }

    private fun updatePropertyDetailsUi(name: String){
        if(name == getString(R.string.house) || name == getString(R.string.rental)){
            binding.noOfBedrooms.visibility = View.VISIBLE
            binding.noOfBathrooms.visibility = View.VISIBLE
            binding.noOfParking.visibility = View.VISIBLE
            binding.size.visibility = View.VISIBLE
            binding.noOfBedroomsLabel.visibility = View.VISIBLE
            binding.noOfBathroomsLabel.visibility = View.VISIBLE
            binding.noOfParkingLabel.visibility = View.VISIBLE
            binding.sizeLabel.visibility = View.VISIBLE
        }
        else if(name == getString(R.string.land)){
            binding.noOfBedrooms.visibility = View.GONE
            binding.noOfBathrooms.visibility = View.GONE
            binding.noOfParking.visibility = View.GONE
            binding.noOfBedroomsLabel.visibility = View.GONE
            binding.noOfBathroomsLabel.visibility = View.GONE
            binding.noOfParkingLabel.visibility = View.GONE
            binding.size.visibility = View.VISIBLE
            binding.sizeLabel.visibility = View.VISIBLE

        }
    }
    private fun redirectSelectUser(){
        val bundle = Bundle().apply {
            putParcelable("location", arguments?.getParcelable("location"))
            putParcelableArrayList("selected_images", arguments?.getParcelableArrayList<Uri>("selected_images"))
            putParcelable("property", getPropertyData())
            putParcelable("user", arguments?.getParcelable("user"))
        }
        val selectedImages = SelectUserFragment()
        selectedImages.arguments = bundle

        changeCurrentFragment(selectedImages)
    }

    private fun redirectUpload(){
        val bundle = Bundle().apply {
            putParcelable("location", arguments?.getParcelable("location"))
            putParcelableArrayList("selected_images", arguments?.getParcelableArrayList<Uri>("selected_images"))
            putParcelable("property", getPropertyData())
            putParcelable("user", arguments?.getParcelable("user"))
        }
        val uploadImagesFragment = UploadImagesFragment()
        uploadImagesFragment.arguments = bundle

        changeCurrentFragment(uploadImagesFragment)
    }

    private fun redirectToLocation(){
        val bundle = Bundle().apply {
            putParcelable("location", arguments?.getParcelable("location"))
            putParcelableArrayList("selected_images", arguments?.getParcelableArrayList<Uri>("selected_images"))
            putParcelable("property", getPropertyData())
            putParcelable("user", arguments?.getParcelable("user"))
        }
        val searchLocationFragment = SearchLocationFragment()
        searchLocationFragment.arguments = bundle

        changeCurrentFragment(searchLocationFragment)
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

    private fun getPropertyData(): Property {
        val propName = binding.propertyName.text.toString()
        val pType = propType
        val priceInput = binding.propertyPrice.text.toString()
        val description = binding.propertyDescription.text.toString()
        val bedInput = binding.noOfBedrooms.text.toString()
        val bathInput = binding.noOfBathrooms.text.toString()
        val parkingInput = binding.noOfParking.text.toString()
        val sizeInput = binding.size.text.toString()

        // Check if priceInput and sizeInput are empty before parsing
        val price: Double = if (priceInput.isNotEmpty()) {
            priceInput.toDouble()
        } else {
            0.0  // Default value or handle the error appropriately
        }

        val size: Int = if (sizeInput.isNotEmpty()) {
            sizeInput.toInt()
        } else {
            0  // Default value or handle the error appropriately
        }


        val bed: Int = if (bedInput.isNotEmpty()) {
            bedInput.toInt()
        } else {
            0  // Default value or handle the error appropriately
        }

        val bath: Int = if (bathInput.isNotEmpty()) {
            bedInput.toInt()
        } else {
            0  // Default value or handle the error appropriately
        }

        val parking: Int = if (parkingInput.isNotEmpty()) {
            parkingInput.toInt()
        } else {
            0  // Default value or handle the error appropriately
        }

        val property = Property(
            title = propName,                   // Title of the property
            propertyType = pType,            // Type of property (e.g., 'house', 'apartment')
            price = price,                   // Price of the property
            description = description,             // Description of the property
            rooms = bed,                      // Number of rooms
            bathrooms = bath,                // Number of bathrooms
            parking = parking,                    // Number of parking spaces
            size = size                     // Size of the property in square meters
        )

        return property
    }

    private fun createProperty(){
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            // Observe the view model to get transactions based on the user ID
            observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            // Handle case when the token is not available (e.g., show error or redirect)
        }
    }


    // Method to observe the ViewModel for transaction-related data and status updates
    private fun observeViewModel(token: String, userId: String) {
        Log.d("token", "token: $token")

        val progressDialog = dialog.showProgressDialog(requireContext())

        // Retrieve selected images
        val selectedImages = arguments?.getParcelableArrayList<Uri>("selected_images")

        val owner = arguments?.getParcelable<User>("user")

        // Validate user input before sending data to the server
        if (!validateProperty(getPropertyData(), selectedImages, owner)) {
            progressDialog.dismiss()
            dialog.showAlertDialog(requireContext(), errorMessage)
            errorMessage = ""
            return
        }

        // Prepare image parts for the request
        // Prepare image parts for the request
        val imageParts = selectedImages?.mapNotNull { uri ->
            val file = getFileFromUri(uri)
            if (file?.exists() == true) {
                Log.d("image", "Image file exists: ${file.absolutePath}")
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file) // Adjust the MIME type as needed
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            } else {
                Log.d("image", "Image file does not exist or cannot be created from URI: ${uri}")
                null
            }
        } ?: emptyList()

        val newProperty = getPropertyData()
        newProperty.agentId = userId

        val locationResult = arguments?.getParcelable<LocationResult>("location")

        if(locationResult != null){
            val location = Location()
            location.address = locationResult.display_name
            location.latitude = locationResult.lat.toDouble()
            location.longitude = locationResult.lon.toDouble()
            newProperty.location = location
        }

        if(owner != null){
            newProperty.ownerId = owner.id
        }


        // Observe the status of the transaction fetching operation
        controller.status.observe(viewLifecycleOwner) { status ->
            if (status) {
                Log.d("status", "successful")
                // Optionally dismiss progress dialog here
                dialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.property_create_successful), hideProgressBar = true)
                progressDialog.dismiss()
                changeCurrentFragment(AgentPropertiesFragment())
            } else {
                dialog.updateProgressDialog(requireContext(), progressDialog, getString(R.string.property_create_fail), hideProgressBar = true)
                progressDialog.dismiss()
                Log.d("status", "fail")
                // Optionally handle failure case (e.g., show an error message)
            }
        }

        // Observe any messages from the ViewModel
        controller.message.observe(viewLifecycleOwner) { message ->
            Log.d("Valuations message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Log.d("failed retrieval", "Retry...")

                progressDialog.dismiss()
                Dialogs().showTimeoutDialog(requireContext()) {
                    Dialogs().showProgressDialog(requireContext())
                    controller.createProperty(token, newProperty, imageParts) // Pass images for retry
                }
            }
        }

        // Initial call to fetch all transactions for the user
        controller.createProperty(token, newProperty, imageParts) // Pass images on initial call
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            // Use ContentResolver to get an InputStream from the content Uri
            val inputStream = context?.contentResolver?.openInputStream(uri)
            inputStream?.let {
                // Create a temporary file in cache directory
                val tempFile = File.createTempFile("image", ".jpg", context?.cacheDir)
                Log.d("image", "Temporary file created at: ${tempFile.absolutePath}")

                // Write the input stream data to the temporary file
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile // Return the file
            }
        } catch (e: Exception) {
            Log.e("image", "Failed to resolve content URI: $uri", e)
            null
        }
    }

    private fun validateProperty(property: Property, selectedImages: List<Uri>?, owner: User?): Boolean {
        var errors = 0

        val locationResult = arguments?.getParcelable<LocationResult>("location")

        // Check required fields
        if (property.title.isEmpty()) {
            Log.d("propertyName", "propertyName is empty")
            errorMessage += "${getString(R.string.enter_property_name)}\n"
            errors++
        }

        if(owner == null){
            Log.d("owner", "owner is empty")
            errorMessage += "${getString(R.string.enter_property_owner)}\n"
            errors++
        }

        if (locationResult != null) {
            val location = Location()
            location.address = locationResult.display_name
            location.latitude = locationResult.lat.toDouble()
            location.longitude = locationResult.lon.toDouble()
            property.location = location
        } else {
            Log.d("location", "location is empty")
            errorMessage += "${getString(R.string.enter_property_location)}\n"
            errors++
        }

        if (property.price.toString().isEmpty() || property.price <= 0) {
            errorMessage += "${getString(R.string.enter_property_price)}\n"
            Log.d("propertyPrice", "propertyPrice is empty")
            errors++
        }

        if (property.description.isEmpty()) {
            errorMessage += "${getString(R.string.enter_property_description)}\n"

            Log.d("description", "description is empty")
            errors++
        }

        if (property.propertyType.isEmpty()) {
            errorMessage += "${getString(R.string.enter_property_type)}\n"
            Log.d("type", "type is empty")
            errors++
        }

        if (selectedImages.isNullOrEmpty()) {
            Log.d("image", "No images selected")
            errorMessage += "${getString(R.string.enter_property_image)}\n"

            errors++
        }

        // Additional validations based on property type
        when (property.propertyType.lowercase()) {
            "land" -> {
                if (property.size.toString().isEmpty() || property.size.toString().toDoubleOrNull() == null || property.size.toString().toDouble() <= 0) {
                    errorMessage += "${getString(R.string.enter_property_size)}\n"
                    Log.d("size", "size must be entered and greater than zero for land properties")
                    errors++
                }
            }
            "house", "rental" -> {
                if (property.size.toString().isEmpty() || property.size.toString().toDoubleOrNull() == null || property.size.toString().toDouble() <= 0) {
                    errorMessage += "${getString(R.string.enter_property_size)}\n"
                    Log.d("size", "size must be entered and greater than zero for houses or rentals")
                    errors++
                }
                if (property.rooms <= 0) {
                    errorMessage += "${getString(R.string.enter_property_room)}\n"
                    Log.d("rooms", "rooms must be greater than zero for houses or rentals")
                    errors++
                }
                if (property.bathrooms < 0) {
                    errorMessage += "${getString(R.string.enter_property_bathroom)}\n"
                    Log.d("bathrooms", "bathrooms cannot be negative")
                    errors++
                }
                if (property.parking < 0) {
                    errorMessage += "${getString(R.string.enter_property_parking)}\n"
                    Log.d("parking", "parking cannot be negative")
                    errors++
                }
            }
            else -> {
                errorMessage += "${getString(R.string.invalid_property_size)}\n"
                Log.d("type", "Invalid property type")
                errors++
            }
        }

        return errors == 0
    }


    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}