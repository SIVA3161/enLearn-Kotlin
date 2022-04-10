package com.example.enlearn.admin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.enlearn.LoginActivity
import com.example.enlearn.MainActivity
import com.example.enlearn.R
import com.example.enlearn.databinding.AddCategoryDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_category_dialog.*
import kotlinx.android.synthetic.main.admin_activity.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Siva G Gurusamy on 02/Apr/2022
 * email : siva@paxel.co
 */
class AdminDashBoardActivity: AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    var categoryTitleValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.admin_activity)


        progressDialog = ProgressDialog(this)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        verifyUser()

        callAddCategoryBtmSheet()
        logOutAdmin()
        addPdfDetails()
    }


    private fun logOutAdmin() {
        val logOutBtnAdmin = findViewById<ImageButton>(R.id.adminLogOutBtn)

        logOutBtnAdmin.setOnClickListener {
            firebaseAuth.signOut()
            verifyUser()
        }
    }

    private fun callAddCategoryBtmSheet() {
         addCategoryBtn.setOnClickListener {
             if(firebaseAuth == null){
                 Toast.makeText(applicationContext, "You are not logged In", Toast.LENGTH_SHORT).show()
             } else{
                 showAlertDialog()
             }
         }
    }

    private fun showAlertDialog() {
        val popUpCategoryDialog = AddCategoryDialogBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this,R.style.RoundedCornersDialog)
            builder.setView(popUpCategoryDialog.root)

        val popUpAlertDialog = builder.create()
        popUpAlertDialog.show()

        popUpAlertDialog.backBtn.setOnClickListener { popUpAlertDialog.dismiss() }

        popUpAlertDialog.submitBtn.setOnClickListener {
            categoryTitleValue = popUpCategoryDialog.tvEnteredCategory.text.toString().trim()

            if(categoryTitleValue.isEmpty()) {
                Toast.makeText(applicationContext, "Nothing is typed as Category", Toast.LENGTH_SHORT).show()
            } else {
                popUpAlertDialog.dismiss()
                addCategoryTextToDB()
            }
        }

    }

    private fun addCategoryTextToDB() {
        progressDialog.setMessage("Adding Category...")
        progressDialog.dismiss()

        val timeStamp = System.currentTimeMillis()
        val userUID = firebaseAuth.uid

        val categoryHashMap: HashMap<String, Any?> = HashMap()
        categoryHashMap["id"] = timeStamp
        categoryHashMap["exam_category"] = categoryTitleValue
        categoryHashMap["uid"] = userUID
        categoryHashMap["time_stamp"] = timeStamp
        categoryHashMap["users_device_UUID"] = UUID.randomUUID().toString()
        categoryHashMap["userType"] = "admin"

        val dbRef = FirebaseDatabase.getInstance().getReference("ExamCategory")
        dbRef.child(timeStamp.toString())
            .setValue(categoryHashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Added successfuly", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to add category due to ${e.message}", Toast.LENGTH_LONG).show()
            }

    }

    private fun verifyUser() {
        val firebaseUser = firebaseAuth.currentUser

        if(firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // Show admin details on this page
            val adminEmail = firebaseUser.email
            val titleAdmin = findViewById<TextView>(R.id.toolbarTitle)
            val subTitleAdmin = findViewById<TextView>(R.id.toolBarSubTitle)
            titleAdmin.text = getString(R.string.text_admin)
            subTitleAdmin.isVisible = true
            subTitleAdmin.text = adminEmail

        }
    }

    //to add pdf
    private fun addPdfDetails() {
        btnAddPdf.setOnClickListener {
            startActivity(Intent(this, AddPdfActivity::class.java))
            finish()
        }
    }
}