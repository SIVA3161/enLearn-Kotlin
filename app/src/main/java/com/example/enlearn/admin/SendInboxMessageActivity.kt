package com.example.enlearn.admin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.enlearn.R
import com.example.enlearn.databinding.ActivitySendInboxMessageBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_pdf.*
import kotlinx.android.synthetic.main.activity_add_pdf.backBtn
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_send_inbox_message.*

class SendInboxMessageActivity : AppCompatActivity() {

    private lateinit var sendMsgBinding: ActivitySendInboxMessageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var inboxArrayList: ArrayList<ModelInbox>
    private var imgUri: Uri? = null
    private var TAG = "IMG_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendMsgBinding = ActivitySendInboxMessageBinding.inflate(layoutInflater)
        setContentView(sendMsgBinding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadReceivers()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        goBack()

        sendMessageActivity()
    }

    private fun loadReceivers() {
        Log.d(TAG,"loadReceivers : Loading Receiver User IDs")
        inboxArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("InboxBucket")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                inboxArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelInbox::class.java)
                    inboxArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.user_name}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendMessageActivity() {
        sendMsgBinding.tvReceiver.setOnClickListener{
            chooseReceiverDialog()
        }

        sendMsgBinding.btnAttachment.setOnClickListener {
            attachmentPickIntent()
        }

        sendMsgBinding.sendBtn.setOnClickListener {
            validateData()
        }
    }

    private var msgTitle = ""
    private var msgDesc = ""
    private var msgReceiver = ""
    private fun validateData(){
        msgTitle = sendMsgBinding.tipMsgTitle.text.toString().trim()
        msgDesc = sendMsgBinding.tipMsgDesc.text.toString().trim()
        msgReceiver = sendMsgBinding.tvReceiver.text.toString().trim()

        if(msgTitle.isEmpty()){
            Toast.makeText(applicationContext, "Enter Message's title..", Toast.LENGTH_SHORT).show()
            titleMsgTil.error = "Title field shouldn't be empty"
        } else if(msgDesc.isEmpty()){
            Toast.makeText(applicationContext, "Enter Book's description..", Toast.LENGTH_SHORT).show()
            descMsgTil.error = "Description field shouldn't be empty"
        }else if(msgReceiver.isEmpty() || tvReceiver.text == "To"){
            Toast.makeText(applicationContext, "Select Book's category..", Toast.LENGTH_SHORT).show()
            sendMsgBinding.tvReceiver.background = resources.getDrawable(R.drawable.round_outline_error)
            tvReceiver.setTextColor(getColor(R.color.profilePrimaryDark))
            tvReceiver.text = getString(R.string.error_select_category)
        } else if(imgUri == null) {
            sendMsgBinding.tvWarning.isVisible = true
            sendMsgBinding.tvWarning.setTextColor(getColor(R.color.profilePrimaryDark))
            sendMsgBinding.tvWarning.text = getString(R.string.error_not_attached_file)
            Handler().postDelayed({
                sendMsgBinding.tvWarning.isVisible = false
            }, 5000)
        } else {
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(500)
            uploadImgToStorage()
        }
    }

    private fun uploadImgToStorage() {
        Log.d(TAG, "uploadImgToStorage: uploading to storage")

        progressDialog.setMessage("Uploading Img...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()
        val filePathAndName = "InboxBucket/"
        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(imgUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadImgToStorage: Img uploaded now. Getting url...")
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedAttachmentUrl = "${uriTask.result}"
                uploadImgInfoToDB(uploadedAttachmentUrl,timestamp)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadImgToStorage: Failed to upload img due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Failed to upload img due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImgInfoToDB(uploadedAttachmentUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadImgInfoToDB: uploading to DB")
        progressDialog.setMessage("Uploading attachemnt info...")

        val uid = firebaseAuth.uid

        val inboxAttachementHashMap: HashMap<String, Any> = HashMap()
        inboxAttachementHashMap["uid"] = "$uid"
        inboxAttachementHashMap["id"] = "$timestamp"
        inboxAttachementHashMap["msg_title"] = msgTitle
        inboxAttachementHashMap["msg_description"] = msgDesc
        inboxAttachementHashMap["msg_receiver"] = msgReceiver
        inboxAttachementHashMap["msg_category_id"] = receiverId
        inboxAttachementHashMap["attached_img_url"] = uploadedAttachmentUrl
        inboxAttachementHashMap["time_stamp"] = "$timestamp"
        inboxAttachementHashMap["is_msg_viewed"] = 0


        val inboxRef = FirebaseDatabase.getInstance().getReference("InboxBucket")
        inboxRef.child("$timestamp")
        inboxRef.setValue(inboxAttachementHashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadImgInfoToDB: Img uploaded succesfully")
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "uploadImgInfoToDB: Success", Toast.LENGTH_SHORT).show()
                imgUri = null
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadPdfInfoToDB: Failed to upload pdf due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Failed to upload pdf due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var receiverId = ""
    private var receiverTitle = ""
    private fun chooseReceiverDialog() {
        Log.d(TAG,"chooseReceiverDialog : Showing receiver of message dialog")

        val inboxArray = arrayOfNulls<String>(inboxArrayList.size)
        for(i in inboxArray.indices) {
            inboxArray[i] = inboxArrayList[i].user_name
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose receiver")
            .setItems(inboxArray){dialog, which ->
                //handle & get items clicked
                receiverTitle = inboxArrayList[which].user_name
                receiverId = inboxArrayList[which].id.toString()

                sendMsgBinding.tvReceiver.text = receiverTitle
                sendMsgBinding.tvReceiver.setTextColor(getColor(R.color.colorPrimaryDark))
                sendMsgBinding.tvReceiver.background = resources.getDrawable(R.drawable.rounded_outline_enabled)
                Log.d(TAG,"categoryPickDialog : SelectedCategory Id: $receiverId")
                Log.d(TAG,"categoryPickDialog : SelectedCategory Title: $receiverTitle")
            }
            .show()
    }

    private fun attachmentPickIntent() {
        Log.d(TAG,"attachmentPickIntent : starting attachment picking intent")

        val attachmentPickIntent = Intent()
        attachmentPickIntent.type = "/image"
        attachmentPickIntent.action = Intent.ACTION_GET_CONTENT
        attachmentActivityResultLauncher.launch(attachmentPickIntent)
    }

    val attachmentActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "attachment picked")
                imgUri = result.data!!.data
                Toast.makeText(applicationContext, "Attachment picked successfuly", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Nothing has been attached")
                Toast.makeText(applicationContext, "Failed to pick your attachment", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun goBack() {
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,AdminDashBoardActivity::class.java))
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
    }
}