package com.example.enlearn


import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.ColorSpace
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.enlearn.databinding.ActivityMainBinding
import com.example.enlearn.ui.account.ModelUserRepository
import com.example.enlearn.utils.DialogCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var userFullName: String? = null
    private var userEmail: String? = null
    private var userGender: String? = null
    var NAVIGATE_TO_PAGE = ""

//    //to get user data
//    private lateinit var firebaseAuth: FirebaseAuth
//    private var ref = FirebaseDatabase.getInstance().getReference("Users")
//    private lateinit var userRepositoryArrayList: ArrayList<ModelUserRepository>
//    private var TAG = "USER_LIVE_DATA_FROM_FIREBASE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)


        toLoginPage()
    }


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


//    fun getLiveUserData() {
//        userRepositoryArrayList = ArrayList()
//        //init firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()
//        val firebaseUser = firebaseAuth.currentUser
//        firebaseUser?.let {
//            ref.child(it.uid)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        userRepositoryArrayList.clear()
////                        for (ds in snapshot.children){
////                            val model = ds.getValue(ModelUserRepository::class.java)
////                            println("Sakthi: ${model?.user_name}")
////                            println("Sakthi: ${model?.full_name}")
////                            userRepositoryArrayList.add(model!!)
////                            Log.d(TAG,"onDataChange: ${model.user_name}\n${model.full_name}" +
////                                    "\n${model.user_email}\n${model.user_gender}\n")
////                        }
////                        val userDataItems: List<ModelUserRepository> = snapshot.children.map { dataSnapshot ->
////                            dataSnapshot.getValue(ModelUserRepository::class.java)!!
////                        }
////
////                        println("Sakthi : ${userDataItems}")
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//
//        }
//    }
}