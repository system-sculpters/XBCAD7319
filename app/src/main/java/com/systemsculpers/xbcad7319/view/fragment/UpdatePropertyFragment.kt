package com.systemsculpers.xbcad7319.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.systemsculpers.xbcad7319.databinding.FragmentUpdatePropertyBinding
import com.systemsculpers.xbcad7319.view.adapter.PropertyTypeAdapter
import com.systemsculpers.xbcad7319.view.custom.Dialogs
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UpdatePropertyFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentUpdatePropertyBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!

    // Adapter for the RecyclerView to display goals
    private lateinit var propertyTypeAdapter: PropertyTypeAdapter

    private lateinit var controller: PropertyController

    // User and token managers for managing user sessions and authentication
    private lateinit var userManager: UserManager
    private lateinit var tokenManager: TokenManager

    private var propType: String = ""

    private var propertyId= ""

    private var errorMessage = ""

    private lateinit var dialog: Dialogs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentUpdatePropertyBinding.inflate(inflater, container, false)

        controller = ViewModelProvider(this).get(PropertyController::class.java)
        // Get instances of user and token managers
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        dialog = Dialogs()

        setPropertyTypes()

        // Handle incoming arguments
        handleIncomingArguments()

        binding.ownerRedirect.setOnClickListener { redirectSelectUser() }
        binding.locationRedirect.setOnClickListener { redirectToLocation() }
        binding.uploadImageRedirect.setOnClickListener { redirectUpload() }
        binding.submitButton.setOnClickListener { updateProperty() }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.update_property))
    }

    // Handle incoming arguments
    private fun handleIncomingArguments() {
        val selectedUser = arguments?.getParcelable<User>("user")
        selectedUser?.let {
            binding.owner.text = it.fullName
        }

        val selectedImages = arguments?.getParcelableArrayList<Uri>("selected_images")
        selectedImages?.let {
            binding.imageNumber.text = "${it.size} images selected"
        }

        val location = arguments?.getParcelable<LocationResult>("location")
        location?.let {
            binding.location.text = it.display_name
        }

        val property = arguments?.getParcelable<Property>("property")
        property?.let {
            setUpPropertyFields(it)
            propertyId = it.id}
    }

    // Set up property fields from the property argument
    private fun setUpPropertyFields(property: Property) {
        binding.propertyName.setText(property.title)
        binding.propertyPrice.setText(property.price.toString())
        binding.propertyDescription.setText(property.description)
        binding.noOfBedrooms.setText(property.rooms.toString())
        binding.noOfBathrooms.setText(property.bathrooms.toString())
        binding.noOfParking.setText(property.parking.toString())
        binding.size.setText(property.size.toString())

        val propertyTypes = mutableListOf(
            PropertyType(getString(R.string.house), R.drawable.baseline_home_filled_24),
            PropertyType(getString(R.string.rental), R.drawable.rental_icon),
            PropertyType(getString(R.string.land), R.drawable.land_icon)
        )

        val selectedTypeIndex = propertyTypes.indexOfFirst { it.name == property.propertyType }
        if (selectedTypeIndex != -1) {
            val selectedType = propertyTypes[selectedTypeIndex]
            propertyTypeAdapter.setSelectedItem(selectedType)
            propType = selectedType.name
            updatePropertyDetailsUi(propType)
        }

        binding.owner.setText(property.user?.fullName)

        binding.location.setText(property.location.address)

        binding.imageNumber.setText("${property.images?.size} images")
    }

    private fun setPropertyTypes() {
        binding.propertyTypesList.layoutManager = GridLayoutManager(requireContext(), 3)

        propertyTypeAdapter = PropertyTypeAdapter(requireContext()) { selectedPropertyType ->
            propType = selectedPropertyType.name
            updatePropertyDetailsUi(propType)
        }

        binding.propertyTypesList.adapter = propertyTypeAdapter
    }

    private fun updatePropertyDetailsUi(name: String) {
        // Show/hide fields based on property type
        val isHouseOrRental = name == getString(R.string.house) || name == getString(R.string.rental)
        binding.noOfBedrooms.visibility = if (isHouseOrRental) View.VISIBLE else View.GONE
        binding.noOfBathrooms.visibility = if (isHouseOrRental) View.VISIBLE else View.GONE
        binding.noOfParking.visibility = if (isHouseOrRental) View.VISIBLE else View.GONE
        binding.noOfBedroomsLabel.visibility = if (isHouseOrRental) View.VISIBLE else View.GONE
        binding.noOfBathroomsLabel.visibility = if (isHouseOrRental) View.VISIBLE else View.GONE
        binding.noOfParkingLabel.visibility = if (isHouseOrRental) View.VISIBLE else View.GONE
        binding.size.visibility = if (name == getString(R.string.land)) View.VISIBLE else View.GONE
        binding.sizeLabel.visibility = if (name == getString(R.string.land)) View.VISIBLE else View.GONE
    }

    private fun redirectSelectUser() {
        val bundle = createBundle()
        val selectedImages = UpdateSelectUserFragment().apply { arguments = bundle }
        changeCurrentFragment(selectedImages)
    }

    private fun redirectUpload() {
        val bundle = createBundle()
        val uploadImagesFragment = UpdateUploadImagesFragment().apply { arguments = bundle }
        changeCurrentFragment(uploadImagesFragment)
    }

    private fun redirectToLocation() {
        val bundle = createBundle()
        val searchLocationFragment = UpdateSelectLocationFragment().apply { arguments = bundle }
        changeCurrentFragment(searchLocationFragment)
    }

    // Helper function to create a bundle with common arguments
    private fun createBundle(): Bundle {
        return Bundle().apply {
            putParcelable("location", arguments?.getParcelable("location"))
            putParcelableArrayList("selected_images", arguments?.getParcelableArrayList<Uri>("selected_images"))
            putParcelable("property", getPropertyData())
            putParcelable("user", arguments?.getParcelable("user"))
        }
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getPropertyData(): Property {
        val propName = binding.propertyName.text.toString()
        val pType = propType
        val price = binding.propertyPrice.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.propertyDescription.text.toString()
        val bed = binding.noOfBedrooms.text.toString().toIntOrNull() ?: 0
        val bath = binding.noOfBathrooms.text.toString().toIntOrNull() ?: 0
        val parking = binding.noOfParking.text.toString().toIntOrNull() ?: 0
        val size = binding.size.text.toString().toIntOrNull() ?: 0
        var owner = binding.owner.text.toString()

        val selectedUser = arguments?.getParcelable<User>("user")
        selectedUser?.let {
            owner = it.fullName
        }

        val selectedImages = arguments?.getParcelableArrayList<Uri>("selected_images")
        selectedImages?.let {
            binding.imageNumber.text = "${it.size} images selected"
        }

        val location = arguments?.getParcelable<LocationResult>("location")
        location?.let {
            binding.location.text = it.display_name
        }

        val property = arguments?.getParcelable<Property>("property")
        property?.let {
            setUpPropertyFields(it)
            propertyId = it.id
        }

        return Property(
            title = propName,
            propertyType = pType,
            price = price,
            description = description,
            rooms = bed,
            bathrooms = bath,
            parking = parking,
            size = size,
            ownerId = owner,

        )
    }

    private fun updateProperty() {
        val user = userManager.getUser() // Get the current user details
        val token = tokenManager.getToken() // Retrieve the authentication token

        if (token != null) {
            observeViewModel(token, user.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }
    }

    private fun observeViewModel(token: String, userId: String) {
        val selectedImages = arguments?.getParcelableArrayList<Uri>("selected_images")
        val owner = arguments?.getParcelable<User>("user")

        // Validate user input before sending data to the server
        if (!validateProperty(getPropertyData(), selectedImages, owner)) {
            return
        }

        // Prepare image parts for the request
        val imageParts = selectedImages?.mapNotNull { uri ->
            val file = getFileFromUri(uri)
            file?.let {
                Log.d("image", "Image file exists: ${it.absolutePath}")
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                MultipartBody.Part.createFormData("images", it.name, requestFile)
            }
        } ?: emptyList()

        val newProperty = getPropertyData().apply {
            agentId = userId
            location = Location().apply {
                val locationResult = arguments?.getParcelable<LocationResult>("location")
                if (locationResult != null) {
                    address = locationResult.display_name
                    latitude = locationResult.lat.toDouble()
                    longitude = locationResult.lon.toDouble()
                }
            }
            ownerId = owner?.id.toString()
        }

        controller.status.observe(viewLifecycleOwner) { status ->
            if (status) {
                Log.d("status", "successful")
                changeCurrentFragment(AgentPropertiesFragment())
            } else {
                Log.d("status", "fail")
            }
        }

        controller.message.observe(viewLifecycleOwner) { message ->
            Log.d("Valuations message", message)
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                controller.updateProperty(token, propertyId, newProperty, imageParts) // Retry
            }
        }

        controller.updateProperty(token, propertyId, newProperty, imageParts) // Initial call
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            // Use ContentResolver to get an InputStream from the content Uri
            val inputStream = context?.contentResolver?.openInputStream(uri)
            inputStream?.let {
                // Create a temporary file in cache directory
                val tempFile = File.createTempFile("tempImage", ".jpg", context?.cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream) // Copy the InputStream to the temporary file
                }
                tempFile // Return the temporary file
            }
        } catch (e: Exception) {
            Log.e("getFileFromUri", "Error getting file from uri: ${e.message}")
            null // Return null if an error occurs
        }
    }

    private fun validateProperty(property: Property, selectedImages: ArrayList<Uri>?, owner: User?): Boolean {
        // Determine the owner either from property.user or from the passed owner
        val propertyOwner = property.user ?: owner

        // Check if required fields are filled
        return when {
            property.title.isBlank() -> {
                Toast.makeText(context, "Property name is required", Toast.LENGTH_SHORT).show()
                false
            }
            property.price <= 0 -> {
                Toast.makeText(context, "Price must be greater than zero", Toast.LENGTH_SHORT).show()
                false
            }
            property.description.isBlank() -> {
                Toast.makeText(context, "Description is required", Toast.LENGTH_SHORT).show()
                false
            }
            property.images.isNullOrEmpty() && selectedImages.isNullOrEmpty() -> {
                Toast.makeText(context, "At least one image is required", Toast.LENGTH_SHORT).show()
                false
            }
            propertyOwner == null -> {
                Toast.makeText(context, "Owner must be selected", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true // All validations passed
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding reference
    }
}
