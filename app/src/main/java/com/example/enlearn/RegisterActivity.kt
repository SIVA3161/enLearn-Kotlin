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
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    //viewBinding for SignUp registration
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var regUserName = ""
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

    private fun genderFunc() {
        radioGrp.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.radioM) {
                regGender = radioM.text.toString()
                Toast.makeText(applicationContext,radioM.text.toString(),Toast.LENGTH_SHORT).show()
            } else if (checkedId == R.id.radioF) {
                regGender = radioF.text.toString()
                Toast.makeText(applicationContext,radioF.text.toString(),Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun validateDate() {
        regUserName = binding.regUserNameTip.text.toString().trim()
        regUserEmail = binding.regUserEmailTip.text.toString().trim()
        regUserPwd = binding.regUserPwdTip.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(regUserEmail).matches()) {
            binding.regUserEmailTip.error = "Invalid Email format"
        } else if(TextUtils.isEmpty((regUserPwd))) {
            binding.regUserPwdTip.error = "Please enter password"
        } else if(regUserPwd.length < 6) {
            binding.regUserPwdTip.error = "Password must be atleast 6 charecters long "
        } else {
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(regUserEmail,regUserPwd)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"You've created your \naccount with an email ${email}",Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                intent.putExtra("UserFullName",regUserName)
                intent.putExtra("UserEmail",regUserEmail)
                intent.putExtra("UserGender",regGender)
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Registration failed due to ${e.message}", Toast.LENGTH_LONG).show()

            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

}
