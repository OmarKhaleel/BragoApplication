package com.example.bragoproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class ThirdSetupPage : Fragment(R.layout.third_setup_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerBTN = view.findViewById<Button>(R.id.RegisterBTN)

        registerBTN.setOnClickListener {
            val registrationIntent = Intent(requireContext(), Registration::class.java)
            startActivity(registrationIntent)
        }

        val loginBTN = view.findViewById<Button>(R.id.LoginBTN)

        loginBTN.setOnClickListener {
            val loginIntent = Intent(requireContext(), Login::class.java)
            startActivity(loginIntent)
        }
    }
}
