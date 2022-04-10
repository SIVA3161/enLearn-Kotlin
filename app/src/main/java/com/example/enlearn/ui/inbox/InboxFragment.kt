package com.example.enlearn.ui.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.enlearn.R
import com.example.enlearn.databinding.FragmentInboxBinding

class InboxFragment : Fragment() {

    private lateinit var inboxViewModel: InboxViewModel
    private lateinit var inboxAdapter: InboxAdapter
    private var binding: FragmentInboxBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // for RecyclerView
//        rvInboxMsg.layoutManager = LinearLayoutManager(context)
//        inboxAdapter.notifyDataSetChanged()
//        rvInboxMsg.adapter = inboxAdapter
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding?.rvInboxMsg?.layoutManager = LinearLayoutManager(context)

        binding?.rvInboxMsg?.adapter = inboxAdapter
    }
}