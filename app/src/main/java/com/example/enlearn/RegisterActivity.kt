package com.example.enlearn

/**
Created by Siva G Gurusamy on 19-12-2021-Dec'2021 at 09:47 AM
 **/
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.enlearn.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    //viewBinding for SignUp registration
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var regUserFullName = ""
    private var regUserEmail = ""
    private var regUserPwd = ""
    private var regGender = ""

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

}
