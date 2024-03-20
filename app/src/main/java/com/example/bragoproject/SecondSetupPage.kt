package com.example.bragoproject

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class SecondSetupPage: Fragment(R.layout.second_setup_page) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNextPage = view.findViewById<Button>(R.id.SecondNextBTN)

        btnNextPage.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.setCurrentItem(2, true)
        }
    }
}