package com.example.enlearn

/**
Created by Siva G Gurusamy on 19-12-2021-Dec'2021 at 09:16 AM
 **/
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.text.TextUtils
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import com.example.enlearn.databinding.ActivityLoginBinding
import com.example.enlearn.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var userEmail = ""
    private var userPwd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegLogin.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
        }

        //progress of login
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.button.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
          userEmail = binding.userEmailTip.text.toString().trim()
          userPwd  = binding.userPwdTip.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            binding.userEmailTip.error = "Invalid Email format"
        } else if(TextUtils.isEmpty(userPwd)){
            binding.userPwdTip.error = "Please enter password"
        } else {
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPwd)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Successfully LoggedIn as $email",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}",Toast.LENGTH_LONG).show()

            }
    }

    private fun checkUser() {
        FirebaseAuth.getInstance().signOut()
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null) {
            startActivity(Intent(this,HomeFragment::class.java))
            finish()
        }
    }
}
