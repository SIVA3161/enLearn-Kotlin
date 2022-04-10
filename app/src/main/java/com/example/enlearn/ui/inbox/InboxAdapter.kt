package com.example.enlearn.ui.inbox


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.enlearn.databinding.ItemInboxBinding
import com.squareup.picasso.Picasso

/**
 * Created by Siva G Gurusamy on 05/Apr/2022
 * email : siva@paxel.co
 */
class InboxAdapter: RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    private val context: Context
    private val inboxArrayList: ArrayList<InboxModel>
    private lateinit var binding: ItemInboxBinding


    constructor(context: Context, inboxArrayList: ArrayList<InboxModel>) {
        this.context = context
        this.inboxArrayList = inboxArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxHolder {
        binding = ItemInboxBinding.inflate(LayoutInflater.from(context), parent, false)
        return InboxHolder(binding.root)
    }

    override fun onBindViewHolder(holder: InboxHolder, position: Int) {
        val inboxModel = inboxArrayList[position]
        val title = inboxModel.msgTitle
        val desc = inboxModel.msgDesc
        val imgUrl = inboxModel.msgImgUrl
        val msgTime = inboxModel.msgReceivedTimeStamp

        holder.inboxTitle.text = title
        holder.inboxDesc.text = desc
        if(imgUrl.isNotEmpty()){
            binding.imageLayout.isVisible = true
            Picasso.with(context)
                .load(imgUrl)
                .into(holder.inboxImage)
        }
        holder.inboxMsgTime.text = msgTime.toString()
    }

    override fun getItemCount(): Int {
        return inboxArrayList.size
    }

    inner class InboxHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var inboxTitle: TextView = binding.tvInboxMsgTitle
        var inboxDesc: TextView = binding.tvInboxMsgDesc
        var inboxImage: ImageView = binding.imgvInbox
        var inboxMsgTime: TextView = binding.tvInboxMsgTimeStamp
    }


}