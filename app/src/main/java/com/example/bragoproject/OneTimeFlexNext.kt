package com.example.bragoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class OneTimeFlexNext : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profilePictureSIV: ShapeableImageView
    private var selectedImagePath: String ?= null
    private lateinit var selectedIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_time_flex_next)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profilePictureSIV = findViewById(R.id.ProfilePictureSIV)

        userInfo()

        selectedIV = findViewById(R.id.SelectedIV)
        selectedImagePath = intent.getStringExtra("key")
        displaySelectedImage()

        profilePictureSIV.setOnClickListener {
            val profileIntent = Intent(this@OneTimeFlexNext, Profile::class.java)
            startActivity(profileIntent)
        }
    }

    private fun userInfo() {
        val usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getProfilePicture()).into(profilePictureSIV)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun displaySelectedImage() {
        if (selectedImagePath != null) {
            Glide.with(this)
                .load(selectedImagePath)
                .into(selectedIV)
        } else {
//            Do nothing
        }
    }

    fun onBackButtonClick(view: View) {
        finish()
    }
}