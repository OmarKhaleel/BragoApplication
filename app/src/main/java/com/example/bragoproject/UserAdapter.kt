package com.example.bragoproject

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter(private var context: Context, private var users: List<User>, private var isFragment: Boolean = false):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false)

        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = users[position]

        holder.usernameTV.text = user.getUsername()
        holder.fullNameTV.text = user.getFullName()
        Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.profile).into(holder.profilePictureSIV)

        checkFollowingStatus(user.getUid(), holder.followBTN)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val preference = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            preference.putString("profileID", user.getUid())
            preference.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.FrameLayout, Profile()).commit()
        })

        holder.followBTN.setOnClickListener {
            if(holder.followBTN.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                firebaseUser?.uid.let {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if(task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            } else {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                firebaseUser?.uid.let {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if(task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val usernameTV: TextView = itemView.findViewById(R.id.UsernameTV)
        val fullNameTV: TextView = itemView.findViewById(R.id.FullNameTV)
        val profilePictureSIV: ShapeableImageView = itemView.findViewById(R.id.UserProfilePictureSearch)
        val followBTN: Button = itemView.findViewById(R.id.FollowBTN)
    }

    private fun checkFollowingStatus(uid: String, followBTN: Button) {
        val followingReference = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child(uid).exists()) {
                    followBTN.text = "Following"
                    followBTN.setBackgroundResource(R.drawable.second_button_layout)
                    followBTN.setTextColor(ContextCompat.getColor(context, R.color.pool_blue))
                } else {
                    followBTN.text = "Follow"
                    followBTN.setBackgroundResource(R.drawable.first_button_layout)
                    followBTN.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}