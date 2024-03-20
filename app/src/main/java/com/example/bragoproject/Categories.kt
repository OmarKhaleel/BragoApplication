package com.example.bragoproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import java.util.Random

class Categories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val rootView = findViewById<View>(R.id.rootView)
        val seekBar: SeekBar = findViewById(R.id.StepIndicator)
        val flexItemsTV: TextView = findViewById(R.id.FlexItemsTV)

        seekBar.isEnabled = false

        val categoriesGrid: GridView = findViewById(R.id.CategoriesGrid)
        val categories = listOf(
            Category("Art", R.drawable.art_picture),
            Category("Architecture", R.drawable.art_picture),
            Category("Brands", R.drawable.art_picture),
            Category("Cars", R.drawable.art_picture),
            Category("Games", R.drawable.art_picture),
            Category("Health", R.drawable.art_picture),
            Category("Music", R.drawable.art_picture),
            Category("Sports", R.drawable.art_picture),
            Category("NSFW", R.drawable.art_picture),
            Category("Travel", R.drawable.art_picture),
            Category("Food", R.drawable.art_picture),
            Category("AI", R.drawable.art_picture),
        )

        val randomImageUrls = mutableListOf<String>()
        for (i in categories.indices) {
            val randomQueryParam = "rnd=${Random().nextInt()}"
            val imageUrl = "https://random.imagecdn.app/900/1280?$randomQueryParam"
            randomImageUrls.add(imageUrl)
        }

        val adapter = CheckboxAdapter(this, categories, randomImageUrls)
        categoriesGrid.adapter = adapter

        flexItemsTV.text = "${categories.size} Items"

        rootView.setOnTouchListener { _, event ->
            hideKeyboard()
            false
        }

        val nextBTN: Button = findViewById(R.id.NextBTN)
        nextBTN.setOnClickListener {
            val fragmentsHolderIntent = Intent(this, FragmentsHolder::class.java)
            startActivity(fragmentsHolderIntent)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun onFloatingActionButtonClick(view: View) {
        finish()
    }

    inner class CheckboxAdapter(private val context: Context, private val categories: List<Category>, private val randomImageUrls: List<String>) : BaseAdapter() {

        override fun getCount(): Int = categories.size

        override fun getItem(position: Int): Any = categories[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = View.inflate(context, R.layout.grid_item_layout, null)

            val frameLayout = view.findViewById<FrameLayout>(R.id.FrameLayout)
            val categoryImage: ShapeableImageView = view.findViewById(R.id.ImageView)
            val checkbox = view.findViewById<CheckBox>(R.id.Checkbox)
            val heartIcon: ImageView = view.findViewById(R.id.HeartIcon)
            val titleTextView = view.findViewById<TextView>(R.id.CategoryTitleTV)
            val postsTextView = view.findViewById<TextView>(R.id.CategoryPostsTV)

            val imageUrl = randomImageUrls[position]
            Picasso.get().load(imageUrl).into(categoryImage)

            frameLayout.setOnClickListener {
                checkbox.isChecked = !checkbox.isChecked
                if (checkbox.isChecked) {
                    heartIcon.setImageResource(R.drawable.ic_filled_heart)
                } else {
                    heartIcon.setImageResource(R.drawable.ic_dull_heart)
                }
            }

            val category = categories[position]
            titleTextView.text = category.title
            postsTextView.text = "posts"

            return view
        }
    }
}