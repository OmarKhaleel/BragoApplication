package com.example.bragoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager


class TimeSeriesFlexNext : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_series_flex_next)

        viewPager = findViewById(R.id.ViewPager)
        val selectedImagePaths = intent.getStringArrayListExtra("selectedImagePaths")

        // Create a PagerAdapter and set it to the ViewPager
        val adapter = ImagePagerAdapter(this, selectedImagePaths)
        viewPager.adapter = adapter

        // Apply a PageTransformer for the desired effect
        viewPager.setPageTransformer(false, HalfPageTransformer())
    }

    fun onBackButtonClick(view: View) {
        finish()
    }
}