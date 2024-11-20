package com.systemsculpers.xbcad7319.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.systemsculpers.xbcad7319.MainActivity
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.Property
import com.systemsculpers.xbcad7319.databinding.FragmentUpdatePropertyBinding
import com.systemsculpers.xbcad7319.databinding.FragmentUploadImagesBinding
import com.systemsculpers.xbcad7319.view.adapter.ImageAdapter


class UpdateUploadImagesFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentUploadImagesBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!


    private lateinit var imageAdapter: ImageAdapter
    private val selectedImages: MutableList<Uri> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and initialize binding
        _binding = FragmentUploadImagesBinding.inflate(inflater, container, false)


        // Initialize RecyclerView
        setupRecyclerView()

        // Set up click listener for the upload image button
        binding.uploadImage.setOnClickListener {
            openImagePicker()
        }

        binding.submitButton.setOnClickListener {
            redirectBackToCreate()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Called after the view is created. Sets the toolbar title in MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.upload_images))
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(selectedImages, ::removeImage, requireContext())
        binding.imageRecyclerView.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun openImagePicker() {
        // Open image picker for selecting images
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.let {
                if (it.clipData != null) {
                    // Multiple images selected
                    for (i in 0 until it.clipData!!.itemCount) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        selectedImages.add(imageUri)
                    }
                } else if (it.data != null) {
                    // Single image selected
                    val imageUri = it.data!!
                    selectedImages.add(imageUri)
                }
                imageAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun removeImage(imageUri: Uri) {
        selectedImages.remove(imageUri)
        imageAdapter.notifyDataSetChanged()
    }

    private fun redirectBackToCreate(){
        val bundle = Bundle().apply {
            putParcelableArrayList("selected_images", ArrayList(selectedImages))
            putParcelable("location", arguments?.getParcelable("location"))
            putParcelable("user", arguments?.getParcelable("user"))
            putParcelable("property", arguments?.getParcelable<Property>("property"))
        }
        val createPropertyFragment = UpdatePropertyFragment()
        createPropertyFragment.arguments = bundle

        changeCurrentFragment(createPropertyFragment)
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}