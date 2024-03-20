package com.example.bragoproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bragoproject.databinding.ActivityMakePostBinding

class MakePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakePostBinding
    private lateinit var oneTimeFlexTab: TextView
    private lateinit var timeSeriesFlexTab: TextView
    private var selectedTab: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMakePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(OneTimeFlex())

        oneTimeFlexTab = findViewById(R.id.OneTimeFlexTab)
        timeSeriesFlexTab = findViewById(R.id.TimeSeriesTab)

        oneTimeFlexTab.setOnClickListener {
            selectedTab = 1
            selectedTab(1)
        }

        timeSeriesFlexTab.setOnClickListener {
            selectedTab = 2
            selectedTab(2)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun selectedTab(tabNumber: Int) {
        if(tabNumber == 1) {
            replaceFragment(OneTimeFlex())
            oneTimeFlexTab.setTextColor(Color.parseColor("#000000"))
            timeSeriesFlexTab.setTextColor(Color.parseColor("#A2A2A2"))

        } else {
            replaceFragment(TimeSeriesFlex())
            oneTimeFlexTab.setTextColor(Color.parseColor("#A2A2A2"))
            timeSeriesFlexTab.setTextColor(Color.parseColor("#000000"))
        }
    }
}