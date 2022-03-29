package com.example.enlearn.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AccountViewModel : ViewModel() {
//    private lateinit var firebaseAuth: FirebaseAuth
    private val _text = MutableLiveData<String>().apply {
        value = "Account Info"
    }
    val text: LiveData<String> = _text
}