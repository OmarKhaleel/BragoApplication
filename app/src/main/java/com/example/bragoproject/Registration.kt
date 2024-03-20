package com.example.bragoproject

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class Registration : AppCompatActivity() {

    private lateinit var profilePicture: ShapeableImageView
    private lateinit var addPhotoBTN: ImageButton
    private lateinit var fullNameET: EditText
    private lateinit var dateOfBirthTIL: TextInputLayout
    private lateinit var dateOfBirthET: EditText
    private var dateFlag: String = ""
    private val genderOptions = arrayOf("Select an option..", "Male", "Female", "Other")
    private var genderFlag: String = ""
    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val rootView = findViewById<View>(R.id.rootView)
        val seekBar: SeekBar = findViewById(R.id.StepIndicator)

        seekBar.isEnabled = false

        profilePicture = findViewById(R.id.ProfileImage)
        addPhotoBTN = findViewById(R.id.AddPhotoBTN)
        addPhotoBTN.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        val dateOfBirthCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            dateOfBirthCalendar.set(Calendar.YEAR, year)
            dateOfBirthCalendar.set(Calendar.MONTH, month)
            dateOfBirthCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(dateOfBirthCalendar)
        }

        dateOfBirthET = findViewById(R.id.DateOfBirthET)
        dateOfBirthTIL = findViewById(R.id.DateOfBirthTIL)
        dateOfBirthTIL.setOnClickListener {
            DatePickerDialog(this, datePicker, dateOfBirthCalendar.get(Calendar.YEAR), dateOfBirthCalendar.get(Calendar.MONTH), dateOfBirthCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        dateOfBirthET.setOnClickListener {
            DatePickerDialog(this, datePicker, dateOfBirthCalendar.get(Calendar.YEAR), dateOfBirthCalendar.get(Calendar.MONTH), dateOfBirthCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val spinner: Spinner = findViewById(R.id.GenderSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                genderFlag = genderOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        val signUpBTN: Button = findViewById(R.id.SignUpBTN)
        signUpBTN.setOnClickListener {
            createAccount()
        }

        rootView.setOnTouchListener { _, event ->
            hideKeyboard()
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePicture.setImageURI(data?.data)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun onFloatingActionButtonClick(view: View) {
        finish()
    }

    private fun updateLabel(dateOfBirthCalendar: Calendar) {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, java.util.Locale("English", "Jordan"))
        dateOfBirthET.setText(sdf.format(dateOfBirthCalendar.time))
        dateFlag = dateOfBirthET.text.toString()
    }

    private fun createAccount() {
        fullNameET = findViewById(R.id.FullNameET)
        usernameET = findViewById(R.id.UsernameET)
        emailET = findViewById(R.id.EmailET)
        passwordET = findViewById(R.id.PasswordET)

        val fullName: String = fullNameET.text.toString()
        val dateOfBirth: String = dateFlag
        val gender: String = genderFlag
        val username: String = usernameET.text.toString()
        val email: String = emailET.text.toString()
        val password: String = passwordET.text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full name is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(dateOfBirth) -> Toast.makeText(this, "Date of birth is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(gender) -> Toast.makeText(this, "Gender is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Username is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@Registration)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val auth: FirebaseAuth = FirebaseAuth.getInstance()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            saveUserInfo(fullName, dateOfBirth, gender, username, email, progressDialog)
                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            auth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, dateOfBirth: String, gender: String, username: String, email: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullName"] = fullName.lowercase()
        userMap["dateOfBirth"] = dateOfBirth
        userMap["gender"] = gender
        userMap["username"] = username.lowercase()
        userMap["email"] = email
        userMap["bio"] = "Hey I am $fullName, and I'm using Brago."
        userMap["profilePicture"] = "https://firebasestorage.googleapis.com/v0/b/brago-application.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=f0b8a060-9469-4905-9cbe-aadc5b9a9ef2"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been created successfully!", Toast.LENGTH_LONG).show()

                    val categoriesIntent = Intent(this@Registration, Categories::class.java)
                    categoriesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(categoriesIntent)
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