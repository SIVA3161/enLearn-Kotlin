package com.example.enlearn.ui.account

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.enlearn.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private var binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding?.userLogOutBtn?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val dtvUserName: TextView = binding!!.dtvUserName
        accountViewModel.text.observe(viewLifecycleOwner, Observer {
            dtvUserName.text = it
        })

        val dtvUserEmail: TextView = binding!!.dtvUserEmail
        accountViewModel.dtvUserEmail.observe(viewLifecycleOwner, Observer {
            dtvUserEmail.text = it

        })
        return root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }





}