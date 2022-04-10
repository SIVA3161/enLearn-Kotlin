package com.example.enlearn.ui.account



import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.enlearn.MainActivity
import com.example.enlearn.ModuleNavigator
import com.example.enlearn.R
import com.example.enlearn.SplashActivity
import com.example.enlearn.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*


class AccountFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private var binding: FragmentAccountBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accFragView: View = inflater.inflate(R.layout.fragment_account, container, false)

        return accFragView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userLogOut()

    }

    private fun userLogOut() {
        userLogOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val gotoLoginActivity = Intent(context,MainActivity::class.java)
            gotoLoginActivity.putExtra("NAVIGATE_TO_PAGE","LOGIN_PAGE")
            startActivity(gotoLoginActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }





}