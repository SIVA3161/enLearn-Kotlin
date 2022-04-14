package com.example.enlearn.admin

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.enlearn.R
import com.example.enlearn.databinding.ActivityAddPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_pdf.*

class AddPdfActivity : AppCompatActivity() {

    //declarations
    private lateinit var addPdfBinding: ActivityAddPdfBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private var pdfUri: Uri? = null
    private var TAG = "PDF_ADD_TAG"
    private var KEY = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPdfBinding = ActivityAddPdfBinding.inflate(layoutInflater)
        setContentView(addPdfBinding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        pdfActivityView()
        goBack()

     
    }
    
//    private fun renderView(){
//        when(KEY) {
//            "SEND_INBOX_MSG_BY_ADMIN" -> inboxMsgByAdminView()
//            else ->
//        }
//    }

//    private fun inboxMsgByAdminView() {
//        headlineCategory.text = getString(R.string.send_inbox_msg)
//        submitBtn.text = getString(R.string.send)
//        tipPdfTitle.hint = getString(R.string.msg_title)
//        tipPdfDesc.hint = getString(R.string.msg_desc)
//
//    }

    private fun pdfActivityView() {
        addPdfBinding.tvSelectedCategory.setOnClickListener {
            categoryPickDialog()
        }

        addPdfBinding.btnAttachment.setOnClickListener {
            pdfPickIntent()
        }

        addPdfBinding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun loadPdfCategories() {
        Log.d(TAG,"loadPdfCategories : Loading pdf categotries")
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("ExamCategory")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.exam_category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var bookTitle = ""
    private var bookDesc = ""
    private var bookCategory = ""
    private fun validateData(){
        bookTitle = addPdfBinding.tipPdfTitle.text.toString().trim()
        bookDesc = addPdfBinding.tipPdfDesc.text.toString().trim()
        bookCategory = addPdfBinding.tvSelectedCategory.text.toString().trim()

        if(bookTitle.isEmpty()){
            Toast.makeText(applicationContext, "Enter Book's title..", Toast.LENGTH_SHORT).show()
            titlePDFTil.error = "Please type the book title"
        } else if(bookDesc.isEmpty()){
            Toast.makeText(applicationContext, "Enter Book's description..", Toast.LENGTH_SHORT).show()
            descPDFTil.error = "Please type the book description"
        }else if(bookCategory.isEmpty() || tvSelectedCategory.text == "Select Category"){
            Toast.makeText(applicationContext, "Select Book's category..", Toast.LENGTH_SHORT).show()
            addPdfBinding.tvSelectedCategory.background = resources.getDrawable(R.drawable.round_outline_error)
            tvSelectedCategory.setTextColor(getColor(R.color.profilePrimaryDark))
            tvSelectedCategory.text = getString(R.string.error_select_category)
        } else if(pdfUri == null) {
            Toast.makeText(applicationContext, "You've not attached any pdf file \n Kindly attach it by clicking top right corner icon", Toast.LENGTH_SHORT).show()
        } else {
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(500)
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: uploading to storage")

        progressDialog.setMessage("Uploading PDF...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()
        val filePathAndName = "Books/${bookCategory}/${bookTitle}"
        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadPdfToStorage: Pdf uploaded now. Getting url...")
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"
                uploadPdfInfoToDB(uploadedPdfUrl,timestamp)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadPdfToStorage: Failed to upload pdf due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Failed to upload pdf due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDB(uploadedPdfUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadPdfInfoToDB: uploading to DB")
        progressDialog.setMessage("Uploading pdf info...")

        val uid = firebaseAuth.uid

        val pdfHashMap: HashMap<String, Any> = HashMap()
        pdfHashMap["uid"] = "$uid"
        pdfHashMap["id"] = "$timestamp"
        pdfHashMap["book_title"] = bookTitle
        pdfHashMap["book_description"] = bookDesc
        pdfHashMap["book_category"] = bookCategory
        pdfHashMap["book_category_id"] = selectedCategoryId
        pdfHashMap["attached_pdf_url"] = uploadedPdfUrl
        pdfHashMap["time_stamp"] = "$timestamp"
        pdfHashMap["views_count"] = 0
        pdfHashMap["downloads_count"] = 0


        val pdfRef = FirebaseDatabase.getInstance().getReference("Books")
        pdfRef.child("$timestamp")
            .setValue(pdfHashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDB: PDF uploaded succesfully")
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "uploadPdfInfoToDB: Success", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadPdfInfoToDB: Failed to upload pdf due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Failed to upload pdf due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog() {
        Log.d(TAG,"categoryPickDialog : Showing pdf category pick dialog")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for(i in categoriesArray.indices) {
            categoriesArray[i] = categoryArrayList[i].exam_category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which ->
                //handle & get items clicked
                selectedCategoryTitle = categoryArrayList[which].exam_category
                selectedCategoryId = categoryArrayList[which].id.toString()

                addPdfBinding.tvSelectedCategory.text = selectedCategoryTitle
                addPdfBinding.tvSelectedCategory.setTextColor(getColor(R.color.colorPrimaryDark))
                addPdfBinding.tvSelectedCategory.background = resources.getDrawable(R.drawable.rounded_outline_enabled)
                Log.d(TAG,"categoryPickDialog : SelectedCategory Id: $selectedCategoryId")
                Log.d(TAG,"categoryPickDialog : SelectedCategory Title: $selectedCategoryTitle")
            }
            .show()
    }

    private fun pdfPickIntent(){
        Log.d(TAG,"pdfPickIntent : starting pdf picking intent")

        val pdfPickIntent = Intent()
        pdfPickIntent.type = "application/pdf"
        pdfPickIntent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(pdfPickIntent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF picked")
                pdfUri = result.data!!.data
                Toast.makeText(applicationContext, "Pdf picked successfuly", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "PDF Canceled")
                Toast.makeText(applicationContext, "Failed to pick your pdf", Toast.LENGTH_SHORT).show()
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