package com.example.bragoproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [Explore.newInstance] factory method to
 * create an instance of this fragment.
 */
class Explore : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var users: MutableList<User>? = null
    private lateinit var searchET: EditText
    private lateinit var homeIB: ImageButton
    private lateinit var searchIB: ImageButton
    private lateinit var profilePictureSIV: ShapeableImageView
    private lateinit var firebaseUser: FirebaseUser

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
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        profilePictureSIV = view.findViewById(R.id.ProfilePictureSIV)

        userInfo()

        homeIB = view.findViewById(R.id.HomeIB)
        homeIB.setOnClickListener {
            val fragmentToLaunch = Home()

            // Get the FragmentManager
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

            // Begin a transaction to replace the current fragment with the new one
            fragmentManager.beginTransaction()
                .replace(R.id.FrameLayout, fragmentToLaunch)
                .addToBackStack(null) // Optional: Add to back stack for navigation
                .commit()
        }

        profilePictureSIV.setOnClickListener {
            val fragmentToLaunch = Profile()

            // Get the FragmentManager
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

            // Begin a transaction to replace the current fragment with the new one
            fragmentManager.beginTransaction()
                .replace(R.id.FrameLayout, fragmentToLaunch)
                .addToBackStack(null) // Optional: Add to back stack for navigation
                .commit()
        }

        recyclerView = view.findViewById(R.id.SearchRecyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        users = ArrayList()
        userAdapter = context?.let { UserAdapter(it, users as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter

        searchIB = view.findViewById(R.id.SearchIB)
        searchET = view.findViewById(R.id.SearchET)
        searchIB.setOnClickListener {
            homeIB.visibility = View.GONE
            searchIB.visibility = View.GONE
            searchET.visibility = View.VISIBLE
            searchET.hasFocus()
        }

        searchET.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(searchET.text.toString() == "") {

                } else {
                    recyclerView?.visibility = View.VISIBLE
                    retrieveUsers()
                    searchUser(p0.toString().lowercase())
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        if(!searchET.hasFocus()) {
            searchET.visibility = View.GONE
            homeIB.visibility = View.VISIBLE
            searchIB.visibility = View.VISIBLE
        }

        return view
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

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users?.clear()

                for(snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if(user != null) {
                        users?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveUsers() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(searchET.text.toString() == "") {
                    users?.clear()

                    for(snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if(user != null) {
                            users?.add(user)
                        }
                    }

                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Explore.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Explore().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}