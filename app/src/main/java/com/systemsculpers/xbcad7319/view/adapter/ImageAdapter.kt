package com.systemsculpers.xbcad7319.view.adapter

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.systemsculpers.xbcad7319.R

class ImageAdapter (
    private val images: List<Uri>,
    private val onRemoveClick: (Uri) -> Unit,
    private val context: Context // Context needed for querying file details
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val removeButton: ImageButton = view.findViewById(R.id.removeButton)
        val imageSize: TextView = view.findViewById(R.id.image_size)
        val imageName: TextView = view.findViewById(R.id.image_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = images[position]
        holder.imageView.setImageURI(imageUri)
        holder.removeButton.setOnClickListener {
            onRemoveClick(imageUri)
        }

        // Set the image name and size
        val fileInfo = queryImageDetails(imageUri)
        holder.imageName.text = fileInfo.first // Image name
        holder.imageSize.text = fileInfo.second // Image size
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun queryImageDetails(imageUri: Uri): Pair<String, String> {
        var fileName = "Unknown"
        var fileSize = "Unknown size"

        val cursor = context.contentResolver.query(
            imageUri, null, null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                // Get file name
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex)
                }

                // Get file size
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex >= 0) {
                    val sizeInBytes = it.getLong(sizeIndex)
                    fileSize = formatFileSize(sizeInBytes)
                }
            }
        }

        return Pair(fileName, fileSize)
    }
    private fun formatFileSize(sizeInBytes: Long): String {
        val kb = sizeInBytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> String.format("%d Bytes", sizeInBytes)
        }
    }
}