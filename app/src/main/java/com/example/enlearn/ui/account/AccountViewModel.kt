package com.example.enlearn.ui.account


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountViewModel : ViewModel() {
    private var firebaseAuth: FirebaseAuth
    private var ref = FirebaseDatabase.getInstance().getReference("Users")
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
    val text: LiveData<String> = _text

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
    val dtvUserEmail: LiveData<String> = _displayUsrEmail
}