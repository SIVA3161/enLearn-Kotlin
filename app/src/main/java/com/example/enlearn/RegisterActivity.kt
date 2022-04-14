package com.example.enlearn

/**
Created by Siva G Gurusamy on 19-12-2021-Dec'2021 at 09:47 AM
 **/

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.enlearn.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    //viewBinding for SignUp registration
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private val permission = 101
    private var telephonyManager = ""
    private var regUserFullName = ""
    private var regUserEmail = ""
    private var regUserPwd = ""
    private var regGender = ""
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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogRegister.setOnClickListener {
            onBackPressed()
        }


        //progress of registration
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("We're Creating your account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        genderFunc()
        binding.button.setOnClickListener {
            validateDate()
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

    private fun validateDate() {
        regUserFullName = binding.regUserNameTip.text.toString().trim()
        regUserEmail = binding.regUserEmailTip.text.toString().trim()
        regUserPwd = binding.regUserPwdTip.text.toString().trim()
        genderFunc()

        if (!Patterns.EMAIL_ADDRESS.matcher(regUserEmail).matches()) {
            binding.regUserEmailTip.error = "Invalid Email format"
        } else if(TextUtils.isEmpty((regUserPwd))) {
            binding.regUserPwdTip.error = "Please enter password"
        } else if(regUserPwd.length < 6) {
            binding.regUserPwdTip.error = "Password must be atleast 6 charecters long "
        } else if(regGender.isEmpty()) {
            Toast.makeText(applicationContext, "Gender must be selected", Toast.LENGTH_LONG).show()
        } else {
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(regUserEmail,regUserPwd)
            .addOnSuccessListener {
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"You've created your \naccount with an email ${email}",Toast.LENGTH_LONG).show()
                updateToUserDB()
                progressDialog.dismiss()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Registration failed due to ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateToUserDB() {
        progressDialog.setMessage("Updating to your profile...")

        val timeStamp = System.currentTimeMillis()
        val userUID = firebaseAuth.uid

        val userDataHashMap: HashMap<String, Any?> = HashMap()
        userDataHashMap["uid"] = userUID
        userDataHashMap["user_name"] = "UR${timeStamp.toString().dropLast(3)}"
        userDataHashMap["full_name"] = regUserFullName
        userDataHashMap["user_email"] = regUserEmail
        userDataHashMap["profile_image"] = ""
        userDataHashMap["user_gender"] = regGender
        userDataHashMap["time_stamp"] = timeStamp
        userDataHashMap["users_device_UUID"] = UUID.randomUUID().toString()
        userDataHashMap["userType"] = "user"

        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.child(userUID!!)
            .setValue(userDataHashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                Toast.makeText(this,"Saved successfuly", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed saving user data due to ${e.message}", Toast.LENGTH_LONG).show()
            }

//        // for inbox
//        val inboxRef = FirebaseDatabase.getInstance().getReference("InboxBucket")
//        inboxRef.child(userUID)
//            .setValue(userDataHashMap)
//            .addOnSuccessListener {
//                Toast.makeText(this,"Inbox Subscription done successfuly", Toast.LENGTH_LONG).show()
//            }
//            .addOnFailureListener { e->
//                progressDialog.dismiss()
//                Toast.makeText(this,"Inbox Subscription failed due to ${e.message}", Toast.LENGTH_LONG).show()
//            }
    }

    private fun genderFunc() {
        radioGrp.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.radioM) {
                regGender = radioM.text.toString()
            } else if (checkedId == R.id.radioF) {
                regGender = radioF.text.toString()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permission -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun disconnected() {
        binding.networkStatus.isVisible = true
        binding.dtvNetworkStatus.setTextColor(resources.getColor(R.color.profileAccentColor))
        binding.dtvNetworkStatus.text = getString(R.string.offline_login_reg_page)
        binding.networkStatus.setBackgroundColor(resources.getColor(R.color.profilePrimaryDark))
    }

    private fun connected() {
        binding.networkStatus.isVisible = true
        binding.dtvNetworkStatus.setTextColor(resources.getColor(R.color.black))
        binding.dtvNetworkStatus.text = getString(R.string.back_to_online)
        binding.networkStatus.setBackgroundColor(resources.getColor(R.color.online_color))
        Handler().postDelayed({
            binding.networkStatus.isVisible = false
        }, 2000)

    }

}
