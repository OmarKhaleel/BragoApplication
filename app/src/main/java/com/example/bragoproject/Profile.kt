package com.example.bragoproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileID: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var editIB: ImageButton
    private lateinit var followIB: ImageButton
    private lateinit var followingIB: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val homeImage = view.findViewById<ImageButton>(R.id.HomeImageButton)
        homeImage.setOnClickListener {
            val fragmentToLaunch = Home()

            // Get the FragmentManager
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

            // Begin a transaction to replace the current fragment with the new one
            fragmentManager.beginTransaction()
                .replace(R.id.FrameLayout, fragmentToLaunch)
                .addToBackStack(null) // Optional: Add to back stack for navigation
                .commit()
        }

        editIB = view.findViewById(R.id.EditIB)
        followIB = view.findViewById(R.id.FollowIB)
        followingIB = view.findViewById(R.id.FollowingIB)
        editIB.setOnClickListener {
            val accountSettingsIntent = Intent(requireContext(), AccountSettings::class.java)
            startActivity(accountSettingsIntent)
        }

        followIB.setOnClickListener {
            firebaseUser.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1)
                    .child("Following").child(profileID)
                    .setValue(true)
            }

            firebaseUser.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileID)
                    .child("Followers").child(it1)
                    .setValue(true)
            }
        }

        followingIB.setOnClickListener {
            firebaseUser.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1)
                    .child("Following").child(profileID)
                    .removeValue()
            }

            firebaseUser.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileID)
                    .child("Followers").child(it1)
                    .removeValue()
            }
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val preference = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if(preference != null) {
            this.profileID = preference.getString("profileID", "none").toString()
        }

        followIB = view.findViewById(R.id.FollowIB)
        followingIB = view.findViewById(R.id.FollowingIB)
        if(profileID == firebaseUser.uid) {
            followIB.visibility = View.GONE
            followingIB.visibility = View.GONE
            editIB.visibility = View.VISIBLE
        } else {
            checkFollowAndFollowingButtonStatus()
        }

        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingReference = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1)
                .child("Following")
        }

        followingReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child(profileID).exists()) {
                    editIB.visibility = View.GONE
                    followIB.visibility = View.GONE
                    followingIB.visibility = View.VISIBLE
                } else {
                    editIB.visibility = View.GONE
                    followingIB.visibility = View.GONE
                    followIB.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val followersReference = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileID)
                .child("Followers")

        followersReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val totalFollowersTV = view?.findViewById<TextView>(R.id.TotalFollowersTV)
                    totalFollowersTV?.text = dataSnapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowings() {
        val followingsReference = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileID)
                .child("Following")

        followingsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val totalFollowingsTV = view?.findViewById<TextView>(R.id.TotalFollowingTV)
                    totalFollowingsTV?.text = dataSnapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo() {
        val usersReference = FirebaseDatabase.getInstance().reference.child("Users").child(profileID)

        usersReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<User>(User::class.java)
                    val profilePictureSIV = view?.findViewById<ShapeableImageView>(R.id.ProfilePictureSIV)
                    val fullNameTV = view?.findViewById<TextView>(R.id.FullNameTV)
                    val usernameTV = view?.findViewById<TextView>(R.id.UsernameTV)
                    val bioTV = view?.findViewById<TextView>(R.id.BioTV)
                    Picasso.get().load(user!!.getProfilePicture()).placeholder(R.drawable.profile).into(profilePictureSIV)
                    fullNameTV?.text = user.getFullName()
                    usernameTV?.text = user.getUsername()
                    bioTV?.text = user.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val preference = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        preference?.putString("profileID", firebaseUser.uid)
        preference?.apply()
    }

    override fun onPause() {
        super.onPause()

        val preference = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        preference?.putString("profileID", firebaseUser.uid)
        preference?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val preference = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        preference?.putString("profileID", firebaseUser.uid)
        preference?.apply()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}