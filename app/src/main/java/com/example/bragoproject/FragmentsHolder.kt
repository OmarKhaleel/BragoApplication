package com.example.bragoproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bragoproject.databinding.ActivityFragmentsHolderBinding


class FragmentsHolder : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentsHolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFragmentsHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Home())

        binding.BottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.Home -> replaceFragment(Home())
                R.id.Explore -> replaceFragment(Explore())
                R.id.MakePost -> {
                    val makePostIntent = Intent(this, MakePostActivity::class.java)
                    startActivity(makePostIntent)
                }
                R.id.Notifications -> replaceFragment(Notifications())
                R.id.Inbox -> replaceFragment(Inbox())

                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayout, fragment)
        fragmentTransaction.commit()
    }
}