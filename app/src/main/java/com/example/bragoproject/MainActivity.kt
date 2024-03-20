package com.example.bragoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var pageIndicatorViews: Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val splashLayout = findViewById<RelativeLayout>(R.id.SplashLayout)
        val mainContentLayout = findViewById<RelativeLayout>(R.id.MainContentLayout)

        Handler(Looper.getMainLooper()).postDelayed({
            splashLayout.animate().alpha(0f).setDuration(500).withEndAction {
                splashLayout.visibility = View.GONE
                mainContentLayout.visibility = View.VISIBLE
                mainContentLayout.alpha = 0f
                mainContentLayout.animate().alpha(1f).setDuration(500).start()
            }.start()
        }, 3000) // Delay for 3 seconds

        viewPager = findViewById(R.id.viewPager)
        indicatorLayout = findViewById(R.id.indicatorLayout)

        val fragmentList = listOf(
            FirstSetupPage(),
            SecondSetupPage(),
            ThirdSetupPage()
        )

        val adapter = ViewPagerAdapter(this, fragmentList)
        viewPager.adapter = adapter

        setupPageIndicator(fragmentList.size)
        setPageChangeListener()
    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null) {
            val fragmentsHolderIntent = Intent(this@MainActivity, FragmentsHolder::class.java)
            fragmentsHolderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(fragmentsHolderIntent)
            finish()
        }
    }

    private fun setupPageIndicator(pageCount: Int) {
        pageIndicatorViews = Array(pageCount) { ImageView(this) }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val margin = resources.getDimensionPixelSize(R.dimen.page_indicator_margin)
        layoutParams.setMargins(margin, 0, margin, 0)

        for (i in 0 until pageCount) {
            pageIndicatorViews[i] = ImageView(this)
            pageIndicatorViews[i].setImageResource(R.drawable.page_indicator_inactive)
            indicatorLayout.addView(pageIndicatorViews[i], layoutParams)
        }

        // Set the first page as active
        pageIndicatorViews[0].setImageResource(R.drawable.page_indicator_active)
    }

    private fun setPageChangeListener() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePageIndicator(position)
            }
        })
    }

    private fun updatePageIndicator(currentPosition: Int) {
        for (i in pageIndicatorViews.indices) {
            if (i == currentPosition) {
                pageIndicatorViews[i].setImageResource(R.drawable.page_indicator_active)
            } else {
                pageIndicatorViews[i].setImageResource(R.drawable.page_indicator_inactive)
            }
        }
    }

    inner class ViewPagerAdapter(
        activity: AppCompatActivity,
        private val fragments: List<Fragment>
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}