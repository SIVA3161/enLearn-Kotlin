package com.example.enlearn

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.example.enlearn.admin.AdminDashBoardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * Created by Siva G Gurusamy on 01/Apr/2022
 * email : siva@paxel.co
 */
class SplashActivity: AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val currentProgress = 80
    val completedProgress = 100
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

        setContentView(R.layout.activity_splash)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()


        progressBar.max = 100


        ObjectAnimator.ofInt(progressBar,"progress",currentProgress)
            .setDuration(3000)
            .start()
        validateUserType()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun validateUserType() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseAuth.currentUser == null) {
            ObjectAnimator.ofInt(progressBar,"progress",completedProgress)
                .setDuration(2500)
                .cancel()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            firebaseUser?.let {
                ref.child(it.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userType = snapshot.child("userType").value
                            ObjectAnimator.ofInt(progressBar,"progress",completedProgress)
                                .setDuration(2500)
                                .cancel()
                            when (userType) {

                                "user" -> {
                                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                                    finish()
                                }
                                "admin" -> {
                                    startActivity(Intent(this@SplashActivity,
                                        AdminDashBoardActivity::class.java))
                                    finish()
                                }
                                else -> {
                                    startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("NOT YET IMPLEMENTED")
                        }
                    })
            }
        }

    }

    private fun disconnected() {
        splashAnim.setMargins(0,300,0,0)
        splashAnim.setAnimation("no-mobile-internet.json")
        progressBar.isVisible = false
        dtvSplash.setTextColor(resources.getColor(R.color.offline_color))
        dtvSplash.setMargins(0,500,0,600)
        dtvSplash.text = getString(R.string.whoops_internet_error)
    }

    private fun connected() {
        splashAnim.setMargins(0,0,0,0)
        splashAnim.setAnimation("bot_white_bg.json")
        progressBar.isVisible = true
        dtvSplash.setMargins(0,0,0,0)
        dtvSplash.setTextColor(resources.getColor(R.color.black))
        dtvSplash.text = getString(R.string.splash_text)
    }

    fun View.setMargins(
        left: Int = this.marginLeft,
        top: Int = this.marginTop,
        right: Int = this.marginRight,
        bottom: Int = this.marginBottom,
    ) {
        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(left, top, right, bottom)
        }
    }
}