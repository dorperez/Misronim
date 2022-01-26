package com.dapps.misronim.ui

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.bumptech.glide.Glide
import com.dapps.misronim.R
import com.dapps.misronim.adapters.ChatRecyclerViewAdapter
import com.dapps.misronim.databinding.ActivityChatBinding
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.Message
import com.dapps.misronim.model.User
import com.dapps.misronim.viewmodel.ChatActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*


class ChatActivity : AppCompatActivity() {


    private lateinit var chatActivityRootView: ActivityChatBinding
    private lateinit var chatViewModel: ChatActivityViewModel

    // Values Holder
    private lateinit var userName: String
    private lateinit var userProfilePic: String
    private lateinit var currentUserProfilePic: String
    private lateinit var currentUID: String
    private lateinit var receiverUID: String
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri
    private lateinit var photoURIHolder: Uri


    private lateinit var chooseImageUri: Uri
    private lateinit var chooseImageUriHolder: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatActivityRootView = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatActivityRootView.root)

        chatViewModel = ViewModelProvider(this)[ChatActivityViewModel::class.java]

        val currentUser = chatViewModel.getCurrentUser()

        val hasExtras = intent.hasExtra("senderUID")
        val extra = intent.getStringExtra("senderUID").toString()
        Log.e("HASEXTRAS", "? $hasExtras")
        Log.e("extra", "? $extra")

        if (hasExtras) {


            observeData(currentUser)

        } else {

            intent = intent
            userName = intent.getStringExtra("userName").toString()
            userProfilePic = intent.getStringExtra("userProfilePic").toString()
            currentUserProfilePic = intent.getStringExtra("currentUserProfilePic").toString()
            receiverUID = intent.getStringExtra("userUID").toString()
            currentUID = intent.getStringExtra("currentUserUID").toString()

            setUpperToolBar()
            startRecyclerView()
            retrieveMessages()
            setSeenMessage(receiverUID)

        }


        //Setup ToolBar
        setSupportActionBar(chatActivityRootView.chatAboveToolBar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setBottomToolBar()

        chatActivityRootView.chatAboveToolBar.setNavigationOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View?) {
                finish()
            }
        })

        chatActivityRootView.chatAboveToolBarProfileImage.setOnClickListener {
            val visitProfileIntent = Intent(this, VisitProfileActivity::class.java)
            visitProfileIntent.putExtra("targetUID", receiverUID)
            startActivity(visitProfileIntent)
        }

    }

    private fun observeData(currentUser: LiveData<User>) {

        val extras = intent.extras
        val senderUID = extras?.get("senderUID").toString()
        val userResult = chatViewModel.getChatDataFromNotification(senderUID)

        chatViewModel.getChatDataFromNotificationState()
            .observe(this, object : Observer<AuthState?> {
                override fun onChanged(authState: AuthState?) {

                    when (authState) {

                        is AuthState.Loading -> {
                            chatActivityRootView.chatActivityLoadingLayout.visibility = View.VISIBLE
                        }
                        is AuthState.Success -> {

                            chatActivityRootView.chatActivityLoadingLayout.visibility = View.GONE

                            userName = userResult.value!!.userName.toString()
                            userProfilePic = userResult.value!!.profilePic.toString()
                            currentUserProfilePic = currentUser.value!!.profilePic.toString()
                            receiverUID = userResult.value!!.uid.toString()
                            currentUID = currentUser.value!!.uid.toString()

                            setUpperToolBar()
                            startRecyclerView()
                            retrieveMessages()
                            setBottomToolBar()
                            setSeenMessage(receiverUID)

                        }
                        is AuthState.AuthError -> {
                            chatActivityRootView.chatActivityLoadingLayout.visibility = View.GONE
                            Toast.makeText(this@ChatActivity, "", Toast.LENGTH_SHORT).show()
                        }

                    }


                }
            })

    }

    private fun setSeenMessage(receiverUID: String) {
        chatViewModel.setSeenMessage(receiverUID)
    }

    private fun setBottomToolBar() {

        chatActivityRootView.chatBottomToolBarSendButton.setOnClickListener {

            val messageToSend = chatActivityRootView.chatBottomToolBarEditText.text.toString()

            if (chatActivityRootView.chatBottomToolBarEditText.text.isNotEmpty()) {
                chatViewModel.sendMessageToUser(
                    applicationContext,
                    currentUID,
                    receiverUID,
                    messageToSend
                )
                chatActivityRootView.chatBottomToolBarEditText.text.clear()
            }

        }

        chatActivityRootView.chatBottomToolBarSendImage.setOnClickListener {
            showChooseImageDialog()
        }

    }

    private fun retrieveMessages() {

        chatViewModel.retrieveMessages(currentUID, receiverUID)
            .observe(this, object : Observer<List<Message>?> {
                override fun onChanged(messagesList: List<Message>?) {


                    if (messagesList != null) {

                        val recyclerAdapter = ChatRecyclerViewAdapter()
                        chatActivityRootView.chatRecyclerView.adapter = recyclerAdapter
                        recyclerAdapter.setMessagesList(
                            messagesList,
                            currentUserProfilePic,
                            userProfilePic
                        )

                    }

                }
            })

    }

    private fun setUpperToolBar() {

        chatActivityRootView.chatAboveToolBar.setNavigationOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View?) {
                finish()
            }
        })

        chatActivityRootView.chatAboveToolBarUserName.text = userName
        Glide.with(this).load(userProfilePic)
            .into(chatActivityRootView.chatAboveToolBarProfileImage)

    }

    private fun showChooseImageDialog() {

        val userClickedDialog =
            MaterialAlertDialogBuilder(this, R.style.dialogStyle)

        val arrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        arrayAdapter.add("Take Image")
        arrayAdapter.add("Choose Image From Gallery")

        userClickedDialog.setAdapter(arrayAdapter,
            DialogInterface.OnClickListener { dialog, which ->

                when (which) {
                    // Take Image
                    0 -> {

                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            takeImageFromCamera()
                        }

                    }

                    // Browse Image
                    1 -> {

                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            chooseImageFromGallery()
                        }

                    }
                }
            })

        userClickedDialog.show()
    }

    private fun takeImageFromCamera() {

        photoURI = applicationContext?.let {
            createImageFile().let { it1 ->

                FileProvider.getUriForFile(
                    it,
                    applicationContext.packageName.toString() + ".provider",
                    it1
                )
            }
        }!!
        photoURI.let {
            photoURIHolder = it

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
            startForResultToLoadImage.launch(intent)
        }

    }

    private fun chooseImageFromGallery() {

        chooseImageUri = applicationContext?.let {
            createImageFile().let { it1 ->

                FileProvider.getUriForFile(
                    it,
                    applicationContext.packageName.toString() + ".provider",
                    it1
                )
            }
        }!!
        chooseImageUri.let {
            chooseImageUriHolder = it

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, it)

            chooseImage.launch(intent)
        }


    }

    private var requestStoragePermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chooseImageFromGallery()
        } else {
            Toast.makeText(
                this,
                "Permission is not granted,you can't choose images without storage permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var requestCameraPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takeImageFromCamera()
        } else {
            Toast.makeText(
                this,
                "Permission is not granted,you can't take images without camera permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        // val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_TEMP_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private val startForResultToLoadImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val selectedImage: Uri? = result.data?.data
                    if (selectedImage != null) {
                        // From Gallery
                    } else {
                        // From Camera
                        val bitmap = BitmapFactory.decodeStream(
                            applicationContext?.contentResolver?.openInputStream(photoURI)
                        )
                        Toast.makeText(
                            applicationContext,
                            "Image is uploading and will be send when its ready",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Error:", "Error : ${photoURI}")

                        chatViewModel.uploadImage(applicationContext, photoURI, receiverUID)


                    }
                } catch (error: Exception) {
                    Log.d("Error:", "Error : ${error.localizedMessage}")
                }
            }
        }

    private val chooseImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {

                try {

                    Toast.makeText(
                        applicationContext,
                        "Image is uploading and will be send when its ready",
                        Toast.LENGTH_SHORT
                    ).show()

                    val selectedImage: Uri? = result.data?.data
                    if (selectedImage != null) {
                        chatViewModel.uploadImage(applicationContext, selectedImage, receiverUID)
                    }

                } catch (error: Exception) {
                    Log.d("Error:", "Error : ${error.localizedMessage}")
                }
            }
        }

    private fun startRecyclerView() {

        chatActivityRootView.chatRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        chatActivityRootView.chatRecyclerView.layoutManager = linearLayoutManager


    }


}