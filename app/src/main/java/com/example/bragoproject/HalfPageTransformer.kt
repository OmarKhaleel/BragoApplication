package com.example.bragoproject

import android.view.View
import androidx.viewpager.widget.ViewPager

class HalfPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.translationX = -position * view.width / 2f

        if (position <= -1) {
            view.alpha = 0f
        } else if (position <= 0) {
            view.alpha = 1 + position
        } else if (position <= 1) {
            view.alpha = 1 - position
        } else {
            view.alpha = 0f
        }
    }
}

