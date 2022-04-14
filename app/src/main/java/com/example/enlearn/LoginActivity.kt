package com.example.enlearn

/**
Created by Siva G Gurusamy on 19-12-2021-Dec'2021 at 09:16 AM
 **/
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.enlearn.admin.AdminDashBoardActivity
import com.example.enlearn.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var userEmail = ""
    private var userPwd = ""
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

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
//        checkUser()

        binding.button.setOnClickListener{
            validateData()
        }
    }


    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun disconnected() {
        networkStatus.isVisible = true
        dtvNetworkStatus.setTextColor(resources.getColor(R.color.profileAccentColor))
        dtvNetworkStatus.text = getString(R.string.offline_login_reg_page)
        networkStatus.setBackgroundColor(resources.getColor(R.color.profilePrimaryDark))
    }

    private fun connected() {
        networkStatus.isVisible = true
        dtvNetworkStatus.setTextColor(resources.getColor(R.color.black))
        dtvNetworkStatus.text = getString(R.string.back_to_online)
        networkStatus.setBackgroundColor(resources.getColor(R.color.online_color))
        Handler().postDelayed({
            networkStatus.isVisible = false
        }, 2000)

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
                routeUser()
                progressDialog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Successfully LoggedIn as $email",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}",Toast.LENGTH_LONG).show()

            }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null) {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun routeUser() {
        progressDialog.setMessage("Checking User...")

        val firebaseUser = firebaseAuth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        firebaseUser?.let {
            ref.child(it.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        progressDialog.dismiss()
                        val userType = snapshot.child("userType").value

                        when (userType) {
                            "user" -> {
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                                finish()
                            }
                            "admin" -> {
                                startActivity(Intent(this@LoginActivity, AdminDashBoardActivity::class.java))
                                finish()
                            }
                            else -> {
                                return
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("NOT YET IMPLEMENTED")
                    }
                })
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finishAffinity()
    }
}
