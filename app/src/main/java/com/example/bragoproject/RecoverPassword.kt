package com.example.bragoproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

class RecoverPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        val rootView = findViewById<View>(R.id.rootView)

        val recoverBTN = findViewById<View>(R.id.RecoverBTN)
        recoverBTN.setOnClickListener {
            // Handle Sign In logic here
            Toast.makeText(this, "Recover Clicked", Toast.LENGTH_SHORT).show()
        }

        rootView.setOnTouchListener { _, event ->
            hideKeyboard()
            false
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun onBackButtonClick(view: View) {
        finish()
    }
}