package com.example.enlearn

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.enlearn.databinding.ActivityMainBinding
import com.example.enlearn.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_splash.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var userFullName: String? = null
    private var userEmail: String? = null
    private var userGender: String? = null

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_videos, R.id.navigation_inbox
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


//        tvUserName.text = intent.getStringExtra("UserFullName") ?: ""
//        tvEmail.text = intent.getStringExtra("UserEmail") ?: ""
//        tvGender.text = intent.getStringExtra("UserGender") ?: ""
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
        dtvNetworkStatus.text = getString(R.string.offline)
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



}