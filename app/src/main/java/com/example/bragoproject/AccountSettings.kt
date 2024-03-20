package com.example.bragoproject

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class AccountSettings : AppCompatActivity() {

    private lateinit var profilePictureSIV: ShapeableImageView
    private lateinit var addPhotoIB: ImageButton
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var imageUri: Uri? = null
    private var myUrl = ""
    private var storageProfilePictureReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePictureReference = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        val rootView = findViewById<View>(R.id.rootView)
        val logoutBTN = findViewById<Button>(R.id.LogoutBTN)

        userInfo()

        rootView.setOnTouchListener { _, event ->
            hideKeyboard()
            false
        }

        logoutBTN.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val loginIntent = Intent(this@AccountSettings, Login::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(loginIntent)
            finish()
        }

        val discardChangesImageButton = findViewById<ImageButton>(R.id.DiscardChangesImageButton)
        discardChangesImageButton.setOnClickListener {
            finish()
        }

        profilePictureSIV = findViewById(R.id.ProfilePictureSIV)
        addPhotoIB = findViewById(R.id.AddPhotoIB)
        addPhotoIB.setOnClickListener {
            checker = "clicked"

            ImagePicker.with(this)
                .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        val submitChangesImageButton = findViewById<ImageButton>(R.id.SubmitChangesImageButton)
        submitChangesImageButton.setOnClickListener {
            if(checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imageUri = data?.data
        profilePictureSIV.setImageURI(imageUri)

    }

    private fun updateUserInfoOnly() {
        val fullNameET = findViewById<TextView>(R.id.FullNameET)
        val usernameET = findViewById<TextView>(R.id.UsernameET)
        val bioET = findViewById<TextView>(R.id.BioET)

        if(TextUtils.isEmpty(fullNameET.text.toString())) {
            Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
        } else if(TextUtils.isEmpty(usernameET.text.toString())) {
            Toast.makeText(this, "Please write username first.", Toast.LENGTH_LONG).show()
        } else if(TextUtils.isEmpty(bioET.text.toString())) {
            Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()
        } else {
            val usersReference = FirebaseDatabase.getInstance().reference.child("Users")

            val userMap = HashMap<String, Any>()
            userMap["fullName"] = fullNameET.text.toString().lowercase()
            userMap["username"] = usernameET.text.toString().lowercase()
            userMap["bio"] = bioET.text.toString().lowercase()

            usersReference.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this, "Account information has been updated successfully!", Toast.LENGTH_LONG).show()

            val fragmentsHolderIntent = Intent(this@AccountSettings, FragmentsHolder::class.java)
            startActivity(fragmentsHolderIntent)
            finish()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun userInfo() {
        val usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    val profilePictureSIV = findViewById<ShapeableImageView>(R.id.ProfilePictureSIV)
                    val fullNameET = findViewById<TextView>(R.id.FullNameET)
                    val usernameET = findViewById<TextView>(R.id.UsernameET)
                    val bioET = findViewById<TextView>(R.id.BioET)
                    Picasso.get().load(user!!.getProfilePicture()).placeholder(R.drawable.profile).into(profilePictureSIV)
                    fullNameET?.text = user.getFullName()
                    usernameET?.text = user.getUsername()
                    bioET?.text = user.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun uploadImageAndUpdateInfo() {
        val fullNameET = findViewById<TextView>(R.id.FullNameET)
        val usernameET = findViewById<TextView>(R.id.UsernameET)
        val bioET = findViewById<TextView>(R.id.BioET)

        when {
            imageUri == null -> {
                Toast.makeText(this, "Please select an image first.", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(fullNameET.text.toString()) -> {
                Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(usernameET.text.toString()) -> {
                Toast.makeText(this, "Please write username first.", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(bioET.text.toString()) -> {
                Toast.makeText(this, "Please write your bio first.", Toast.LENGTH_LONG).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile information...")
                progressDialog.show()

                val fileReference = storageProfilePictureReference!!.child(firebaseUser.uid + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileReference.putFile(imageUri!!)

                uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if(!task.isSuccessful) {
                        task.exception?.let {
                            progressDialog.dismiss()
                            throw it
                        }
                    }
                    return@Continuation fileReference.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
                    if(task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val reference = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullName"] = fullNameET.text.toString().lowercase()
                        userMap["username"] = usernameET.text.toString().lowercase()
                        userMap["bio"] = bioET.text.toString().lowercase()
                        userMap["profilePicture"] = myUrl


                        reference.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account information has been updated successfully!", Toast.LENGTH_LONG).show()

                        val fragmentsHolderIntent = Intent(this@AccountSettings, FragmentsHolder::class.java)
                        startActivity(fragmentsHolderIntent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                } )
            }
        }

    }
}