package com.example.enlearn

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.enlearn.databinding.ActivityMainBinding
import com.example.enlearn.databinding.FragmentAccountBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var accountBinding: FragmentAccountBinding

    private var userFullName: String? = null
    private var userEmail: String? = null
    private var userGender: String? = null

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


        // account details rendering
//        accountBinding.logOutBtn.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//        }


//        tvUserName.text = intent.getStringExtra("UserFullName") ?: ""
//        tvEmail.text = intent.getStringExtra("UserEmail") ?: ""
//        tvGender.text = intent.getStringExtra("UserGender") ?: ""
    }


}