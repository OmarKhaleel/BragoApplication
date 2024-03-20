package com.example.bragoproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

class ImagePagerAdapter(private val context: Context, private val imagePaths: List<String>?) :
    PagerAdapter() {

    override fun getCount(): Int {
        return imagePaths?.size ?: 0
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_image, container, false)
        val imageView = itemView.findViewById<View>(R.id.imageView)

        // Load the image using Glide or your preferred image loading library
        Glide.with(context)
            .load(imagePaths?.get(position))
            .into(imageView as ImageView)

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
