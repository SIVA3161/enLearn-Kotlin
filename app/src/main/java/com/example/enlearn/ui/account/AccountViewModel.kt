package com.example.enlearn.ui.account


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.enlearn.admin.ModelCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountViewModel : ViewModel() {

    //declarations
    private var firebaseAuth: FirebaseAuth
    private var ref = FirebaseDatabase.getInstance().getReference("Users")

    private var TAG = "USER_LIVE_DATA_FROM_FIREBASE"


    //userName display
    private val _text = MutableLiveData<String>().apply {
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.let {
            ref.child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userName = snapshot.child("user_name").value
                        value = userName.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("NOT YET IMPLEMENTED")
                    }
                })
        }

    }
    val userName: LiveData<String> = _text

    //userFullName display
    private val _displayUserFullName = MutableLiveData<String>().apply {
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.let {
            ref.child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userName = snapshot.child("full_name").value
                        value = userName.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("NOT YET IMPLEMENTED")
                    }
                })
        }

    }
    val userFullName: LiveData<String> = _displayUserFullName

    //userEmail display
    private val _displayUsrEmail = MutableLiveData<String>().apply {
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.let {
            ref.child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userEmail = snapshot.child("user_email").value
                        value = userEmail.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("NOT YET IMPLEMENTED")
                    }
                })
        }

    }
    val userEmail: LiveData<String> = _displayUsrEmail

    //userGender display
    private val _displayUsrGender = MutableLiveData<String>().apply {
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.let {
            ref.child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userEmail = snapshot.child("user_gender").value
                        value = userEmail.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("NOT YET IMPLEMENTED")
                    }
                })
        }

    }
    val userGender: LiveData<String> = _displayUsrGender

    //userProfileImgUrl display
    private val _displayUsrAvatar = MutableLiveData<String>().apply {
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        firebaseUser?.let {
            ref.child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userEmail = snapshot.child("profile_image").value
                        value = userEmail.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("NOT YET IMPLEMENTED")
                    }
                })
        }

    }
    val userProfileImgUrl: LiveData<String> = _displayUsrAvatar

}