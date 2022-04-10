package com.example.enlearn


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.enlearn.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var userFullName: String? = null
    private var userEmail: String? = null
    private var userGender: String? = null
    var NAVIGATE_TO_PAGE = ""

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

        navView.setupWithNavController(navController)


//        tvUserName.text = intent.getStringExtra("UserFullName") ?: ""
//        tvEmail.text = intent.getStringExtra("UserEmail") ?: ""
//        tvGender.text = intent.getStringExtra("UserGender") ?: ""
        toLoginPage()
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

    private fun toLoginPage() {
      NAVIGATE_TO_PAGE = intent.getStringExtra("NAVIGATE_TO_PAGE").toString()
      when (NAVIGATE_TO_PAGE) {
          "LOGIN_PAGE" -> {
              startActivity(Intent(this,LoginActivity::class.java))
              finish()
          }
          else -> return
      }
  }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }
}