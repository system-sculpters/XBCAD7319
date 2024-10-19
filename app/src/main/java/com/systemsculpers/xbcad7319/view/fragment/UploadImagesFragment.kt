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
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.databinding.FragmentCreatePropertyBinding
import com.systemsculpers.xbcad7319.databinding.FragmentUploadImagesBinding
import com.systemsculpers.xbcad7319.view.adapter.ImageAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UploadImagesFragment : Fragment() {
    // View binding object for accessing views in the layout
    private var _binding: FragmentUploadImagesBinding? = null

    // Non-nullable binding property
    private val binding get() = _binding!!


    private lateinit var imageAdapter: ImageAdapter
    private val selectedImages: MutableList<Uri> = mutableListOf()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
        // Inflate the layout for this fragment
        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        private const val REQUEST_IMAGE_PICK = 1001

//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            UploadImagesFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//        }
    }
}