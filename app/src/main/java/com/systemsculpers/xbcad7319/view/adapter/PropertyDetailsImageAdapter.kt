package com.systemsculpers.xbcad7319.view.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PropertyDetailsImageAdapter (private val imageUrls: List<String>) :
    RecyclerView.Adapter<PropertyDetailsImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Load image using a library like Glide or Picasso
        Glide.with(holder.imageView.context)
            .load(imageUrls[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size

}