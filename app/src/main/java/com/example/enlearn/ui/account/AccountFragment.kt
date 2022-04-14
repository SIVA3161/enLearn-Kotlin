package com.example.enlearn.ui.account




import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.enlearn.*
import com.example.enlearn.R.drawable.user_avatar_placeholder
import com.example.enlearn.databinding.ContentProfileBinding
import com.example.enlearn.databinding.EditProfileDialogBinding
import com.example.enlearn.databinding.FragmentAccountBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.content_profile.*
import kotlinx.android.synthetic.main.edit_profile_dialog.*
import kotlinx.android.synthetic.main.fragment_account.*


class AccountFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private var binding: FragmentAccountBinding? = null
    private var contentBinding: ContentProfileBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    var updateFullNameValue: String = ""
    var updateEmailValue: String = ""
    private var image = 100
    private lateinit var imageUri: Uri
    private lateinit var gallery: Intent

    // to load these below details from firebase
    var userName: String? = null
    var userFullName: String? = null
    var userEmail: String? = null
    var userGender: String? = null
    var userProfileImgUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accFragView: View = inflater.inflate(R.layout.fragment_account, container, false)
        accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)


        //user name
        accountViewModel.userName.observe(viewLifecycleOwner, Observer {
            dtvUserID.text = it
            userName = it
        })
        //user full name
        accountViewModel.userFullName.observe(viewLifecycleOwner, Observer {
            dtvUserFullName.text = it
            userFullName = it
        })
        //user email
        accountViewModel.userEmail.observe(viewLifecycleOwner, Observer {
            dtvUserEmailContent.text = it
            dtvUserEmail.text = it
            userEmail = it
        })
        //user gender
        accountViewModel.userGender.observe(viewLifecycleOwner, Observer {
            dtvGender.text = it
            userGender = it
        })
        accountViewModel.userProfileImgUrl.observe(viewLifecycleOwner, Observer {
            userProfileImgUrl = it
            if(it.isNotEmpty()){
                imgvUserProfileImg.load(it){
                    crossfade(true)
                    placeholder(user_avatar_placeholder)
                    crossfade(500)
                    transformations(CircleCropTransformation())
                }
            }else {
                    imgvUserProfileImg.load(R.drawable.user_avatar_placeholder){
                        crossfade(true)
                        placeholder(user_avatar_placeholder)
                        crossfade(500)
                        transformations(CircleCropTransformation())
                    }
                }
        })

        return accFragView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //init
        progressDialog = ProgressDialog(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        //load required data

        //call methods
        userLogOut()

        editProfileDialog()

    }

    private fun userLogOut() {
        btnUserLogOut.isVisible = true
        btnUserLogOut.setOnClickListener {
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


    // for the edit profile pop-up dialog
    private lateinit var popUpEditProfileAlertDialog: AlertDialog
    private fun editProfileDialog() {
        btnEditProfile.setOnClickListener {
            val popUpEditProfileDialog = EditProfileDialogBinding.inflate(LayoutInflater.from(requireContext()))
            val builder = AlertDialog.Builder(requireContext(),R.style.Theme_Design_BottomSheetDialog)
            builder.setView(popUpEditProfileDialog.root)

            popUpEditProfileAlertDialog = builder.create()
            popUpEditProfileAlertDialog.show()

            popUpEditProfileAlertDialog.closeBtn.setOnClickListener { popUpEditProfileAlertDialog.dismiss() }

            popUpEditProfileAlertDialog.updateBtn.setOnClickListener {
                updateFullNameValue = popUpEditProfileDialog.tvUpdateFullName.text.toString().trim()
                updateEmailValue = popUpEditProfileDialog.tvUpdateEmail.text.toString().trim()

                if(gallery == null) {
                    Toast.makeText(requireContext(), "Please select your profile image form the gallery", Toast.LENGTH_SHORT).show()
                } else {
                    popUpEditProfileAlertDialog.dismiss()
                    uploadProfileImgToStorage()
                }
            }

            popUpEditProfileAlertDialog.btnEditImg.setOnClickListener {
                gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, image)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == image) {
            imageUri = data?.data!!
            popUpEditProfileAlertDialog.imgProfileUpdate.load("${imageUri}"){
                crossfade(true)
                placeholder(user_avatar_placeholder)
                crossfade(1000)
                transformations(CircleCropTransformation())
            }
        }
    }

    private var TAG = "UPLOAD_PROFILE_IMG_TAG"
    private fun uploadProfileImgToStorage(){
        progressDialog.setMessage("Uploading Profile Image...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()
        val filePathAndName = "UserProfileImage/${userName}/${userFullName}Avatar"
        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadProfileImgToStorage: Profile Image uploaded now. Getting url...")
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedProfileImgUrl = "${uriTask.result}"
                updateUserDataToRDB(uploadedProfileImgUrl,timestamp)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadProfileImgToStorage: Failed to upload img due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to upload pdf due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserDataToRDB(uploadedProfileImgUrl: String, timestamp: Long) {
        progressDialog.setMessage("Updating user profile...")
        progressDialog.dismiss()

        val userUID = firebaseAuth.uid

//        val updateUserDataHashMap: HashMap<String, Any?> = HashMap()
//        updateUserDataHashMap["full_name"] = updateFullNameValue
//        updateUserDataHashMap["user_email"] = updateFullNameValue
//        updateUserDataHashMap["profile_image"] = uploadedProfileImgUrl
//        updateUserDataHashMap["profile_updated_at"] = timestamp


        val dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child("Users")
            .child(userUID!!)
            .child("profile_image")
            .setValue(uploadedProfileImgUrl)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(),"Updated successfuly", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(requireContext(),"Failed to update your data due to ${e.message}", Toast.LENGTH_LONG).show()
            }

    }



}