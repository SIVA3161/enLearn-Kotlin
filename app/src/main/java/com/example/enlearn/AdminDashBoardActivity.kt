package com.example.enlearn

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.tool_bar.*

/**
 * Created by Siva G Gurusamy on 02/Apr/2022
 * email : siva@paxel.co
 */
class AdminDashBoardActivity: AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.admin_activity)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        verifyUser()


        logOutAdmin()

    }

    private fun logOutAdmin() {
        val logOutBtnAdmin = findViewById<ImageButton>(R.id.adminLogOutBtn)

        logOutBtnAdmin.setOnClickListener {
            firebaseAuth.signOut()
            verifyUser()
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
            titleAdmin.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            titleAdmin.text = getString(R.string.text_admin)
            subTitleAdmin.isVisible = true
            subTitleAdmin.text = adminEmail

        }
    }
}