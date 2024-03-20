package com.example.bragoproject

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val rootView = findViewById<View>(R.id.rootView)

        val tvForgotPassword = findViewById<View>(R.id.ForgotPasswordTV)
        tvForgotPassword.setOnClickListener {
            val recoveryIntent = Intent(this, RecoverPassword::class.java)
            startActivity(recoveryIntent)
        }

        val btnSignIn = findViewById<View>(R.id.SignInBTN)
        btnSignIn.setOnClickListener {
            signInUser()
        }

        val tvSignUp = findViewById<View>(R.id.SignUpTV)
        tvSignUp.setOnClickListener {
            val registrationIntent = Intent(this, Registration::class.java)
            startActivity(registrationIntent)
        }

        rootView.setOnTouchListener { _, event ->
            hideKeyboard()
            false
        }
    }

    private fun signInUser() {
        emailET = findViewById(R.id.EmailET)
        passwordET = findViewById(R.id.PasswordET)

        val email: String = emailET.text.toString()
        val password: String = passwordET.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@Login)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val auth: FirebaseAuth = FirebaseAuth.getInstance()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            progressDialog.dismiss()

                            val fragmentsHolderIntent = Intent(this@Login, FragmentsHolder::class.java)
                            fragmentsHolderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(fragmentsHolderIntent)
                            finish()
                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            progressDialog.dismiss()
                        }
                }
            }
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

//    TODO
    fun onGmailIconClick(view: View) {
        val googleIntent = Intent(Intent.ACTION_VIEW, Uri.parse("your_gmail_url_here"))
        startActivity(googleIntent)
    }

    fun onFacebookIconClick(view: View) {
        val facebookIntent = Intent(Intent.ACTION_VIEW, Uri.parse("your_facebook_url_here"))
        startActivity(facebookIntent)
    }

    fun onInstagramIconClick(view: View) {
        val instagramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("your_instagram_url_here"))
        startActivity(instagramIntent)
    }
}