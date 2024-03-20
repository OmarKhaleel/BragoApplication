package com.example.bragoproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide

class TimeSeriesFlex : Fragment() {

    private lateinit var galleryRecyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter
    private val galleryList = ArrayList<String>()
    private lateinit var selectedIV: ImageView
    private lateinit var maxSelectionTV: TextView
    private var selectedImagePaths = ArrayList<String>() // Store the selected images paths
    private lateinit var backIB: ImageButton
    private lateinit var nextBTN: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_series_flex, container, false)

        maxSelectionTV = view.findViewById(R.id.MaxSelectionTV)
        selectedIV = view.findViewById(R.id.SelectedIV)
        galleryRecyclerView = view.findViewById(R.id.GalleryRecyclerView)
        galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        galleryAdapter = GalleryAdapter()
        galleryRecyclerView.adapter = galleryAdapter

        if (checkPermission()) {
            loadMedia()
        } else {
            requestPermission()
        }

        backIB = view.findViewById(R.id.X_BTN)
        backIB.setOnClickListener {
            val fragmentsHolderIntent = Intent(requireContext(), FragmentsHolder::class.java)
            startActivity(fragmentsHolderIntent)
        }

        nextBTN = view.findViewById(R.id.NextBTN)
        nextBTN.setOnClickListener {
            val timeSeriesFlexNextIntent = Intent(requireContext(), TimeSeriesFlexNext::class.java)
            timeSeriesFlexNextIntent.putStringArrayListExtra("selectedImagePaths", selectedImagePaths)
            startActivity(timeSeriesFlexNextIntent)
        }

        return view
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    private fun loadMedia() {
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )

        val selection = "${MediaStore.Images.Media.DATA} IS NOT NULL"

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        while (cursor != null && cursor.moveToNext()) {
            val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = cursor.getString(columnIndex)
            galleryList.add(path)
        }

        cursor?.close()

        galleryAdapter.notifyDataSetChanged()
    }

    private fun displaySelectedImage() {
        if (selectedImagePaths.isNotEmpty()) {
            // Display the first selected image
            val selectedImagePath = selectedImagePaths.first()
            Glide.with(this)
                .load(selectedImagePath)
                .into(selectedIV)
        } else {
            // No image selected, hide the ImageView
            selectedIV.setImageResource(0)
        }
    }

    private fun updateMaxSelectionTVVisibility() {
        if (selectedImagePaths.isNotEmpty()) {
            maxSelectionTV.visibility = View.GONE
        } else {
            maxSelectionTV.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(): OneTimeFlex {
            return OneTimeFlex()
        }
    }

    inner class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
            return GalleryViewHolder(view)
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            val mediaPath = galleryList[position]
            Glide.with(holder.itemView)
                .load(mediaPath)
                .into(holder.itemIV)

            // Handle item click to select/deselect the image
            holder.itemView.setOnClickListener {
                if (selectedImagePaths.contains(mediaPath)) {
                    selectedImagePaths.remove(mediaPath) // Deselect the image
                } else {
                    selectedImagePaths.add(mediaPath) // Select the image
                }
                displaySelectedImage()
                notifyDataSetChanged()
                updateMaxSelectionTVVisibility()
            }

            // Highlight the selected item
            if (selectedImagePaths.contains(mediaPath)) {
                // Set a border or background to indicate selection
                holder.itemIV.setColorFilter(Color.argb(150, 255, 255, 255), PorterDuff.Mode.MULTIPLY)
            } else {
                holder.itemIV.clearColorFilter()
            }
        }

        override fun getItemCount(): Int {
            return galleryList.size
        }

        inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemIV: ImageView = itemView.findViewById(R.id.ItemIV)
        }
    }
}

