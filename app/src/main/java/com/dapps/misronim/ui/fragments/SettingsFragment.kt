package com.dapps.misronim.ui.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dapps.misronim.R
import com.dapps.misronim.databinding.FragmentSettingsBinding
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.User
import com.dapps.misronim.viewmodel.SettingsFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SettingsFragment : Fragment() {

    private lateinit var rootView: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsFragmentViewModel
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = ViewModelProvider(this)[SettingsFragmentViewModel::class.java]



        settingsViewModel.getCurrentUser().observe(this, object : Observer<User?> {
            override fun onChanged(user: User?) {


                if (user != null) {

                    currentUser = user

                    // Cover Pic
                    Glide.with(this@SettingsFragment).load(user.coverPic)
                        .into(rootView.settingsCoverImage)

                    //Profile Pic
                    Glide.with(this@SettingsFragment).load(user.profilePic)
                        .into(rootView.settingsProfileImage)

                    //UserName
                    rootView.settingsUserName.text = user.userName

                }

            }
        })


    }

    private fun displaySocialDialog(view: View) {

        val builderSingle =
            MaterialAlertDialogBuilder(requireContext(), com.dapps.misronim.R.style.dialogStyle)


        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        arrayAdapter.add("View")
        arrayAdapter.add("Edit")


        when (view) {
            rootView.settingsFacebookButton -> {
                builderSingle.setTitle("Facebook Url")
            }
            rootView.settingsInstagramButton -> {
                builderSingle.setTitle("Instagram Url")
            }
            rootView.settingsWebButton -> {
                builderSingle.setTitle("Website Url")
            }
        }

        builderSingle.setAdapter(arrayAdapter,
            DialogInterface.OnClickListener { dialog, which ->

                when (which) {
                    0 -> {

                        // View
                        when (view) {
                            rootView.settingsFacebookButton -> {

                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(currentUser.facebook))
                                startActivity(browserIntent)

                            }
                            rootView.settingsInstagramButton -> {

                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(currentUser.instagram))
                                startActivity(browserIntent)

                            }
                            rootView.settingsWebButton -> {

                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(currentUser.website))
                                startActivity(browserIntent)

                            }

                        }

                    }

                    // Edit
                    1 -> {

                        val editDialog =
                            MaterialAlertDialogBuilder(view.context, R.style.dialogStyle)
                        editDialog.setTitle("Edit Profile Links")
                        editDialog.setMessage("What is the new URL ?")

                        val editText = EditText(context)
                        editText.hint = "Please enter URL"


                        val container = context?.let { FrameLayout(it) }
                        val params = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        params.leftMargin = 25
                        params.rightMargin = 25

                        editText.layoutParams = params
                        container?.addView(editText)
                        editDialog.setView(container)


                        editDialog.setNegativeButton(
                            "Cancel",
                            DialogInterface.OnClickListener { dialog2, which2 ->

                                dialog2.dismiss()

                            })

                        editDialog.setPositiveButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog2, which2 ->

                                val newLink = editText.text.toString()


                                if (editText.text.isNotEmpty() && URLUtil.isValidUrl(newLink)) {

                                    when (view) {
                                        rootView.settingsFacebookButton -> {

                                            settingsViewModel.setNewFacebook(newLink).observe(
                                                viewLifecycleOwner,
                                                object : Observer<AuthState?> {
                                                    override fun onChanged(authState: AuthState?) {

                                                        when (authState) {

                                                            is AuthState.Success -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Your facebook url updated successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            is AuthState.AuthError -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    authState.message,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                        }
                                                    }
                                                })

                                        }
                                        rootView.settingsInstagramButton -> {

                                            settingsViewModel.setNewInstagram(newLink).observe(
                                                viewLifecycleOwner,
                                                object : Observer<AuthState?> {
                                                    override fun onChanged(authState: AuthState?) {

                                                        when (authState) {

                                                            is AuthState.Success -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Your instagram url updated successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            is AuthState.AuthError -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    authState.message,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                        }
                                                    }
                                                })

                                        }
                                        rootView.settingsWebButton -> {

                                            settingsViewModel.setNewWebPage(newLink).observe(
                                                viewLifecycleOwner,
                                                object : Observer<AuthState?> {
                                                    override fun onChanged(authState: AuthState?) {

                                                        when (authState) {

                                                            is AuthState.Success -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Your website url updated successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            is AuthState.AuthError -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    authState.message,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                        }
                                                    }
                                                })

                                        }
                                    }

                                } else {

                                    if (editText.text.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "Please enter URL",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else if (!URLUtil.isValidUrl(newLink)) {
                                        Toast.makeText(
                                            context,
                                            "Please enter valid URL",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

                                }


                            })

                        editDialog.show()

                    }

                }


            })
        builderSingle.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = FragmentSettingsBinding.inflate(LayoutInflater.from(context), container, false)

        rootView.settingsFacebookButton.setOnClickListener {
            displaySocialDialog(it)
        }
        rootView.settingsInstagramButton.setOnClickListener {
            displaySocialDialog(it)
        }
        rootView.settingsWebButton.setOnClickListener {
            displaySocialDialog(it)
        }

        rootView.settingsProfileImage.setOnClickListener {
            displayProfileImageDialog(it)
        }
        rootView.settingsCoverImage.setOnClickListener {
            displayProfileImageDialog(it)
        }


        return rootView.root
    }

    private fun displayProfileImageDialog(view: View) {

        val builderSingle =
            MaterialAlertDialogBuilder(requireContext(), com.dapps.misronim.R.style.dialogStyle)


        val arrayAdapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        arrayAdapter.add("View")
        arrayAdapter.add("Edit")

        if (view == rootView.settingsProfileImage) {
            builderSingle.setTitle("Profile Image")
        } else if (view == rootView.settingsCoverImage) {
            builderSingle.setTitle("Cover Image")
        }

        builderSingle.setAdapter(arrayAdapter,
            DialogInterface.OnClickListener { dialog, which ->

                when (which) {

                    0 -> {

                        // View
                        when (view) {

                            rootView.settingsProfileImage -> {

                                val imageBundle = Bundle()
                                imageBundle.putString("image", currentUser.profilePic)
                                startBiggerImage(imageBundle)

                            }

                            rootView.settingsCoverImage -> {

                                val imageBundle = Bundle()
                                imageBundle.putString("image", currentUser.coverPic)
                                startBiggerImage(imageBundle)

                            }


                        }

                    }
                    //Edit
                    1 -> {

                        when (view) {

                            rootView.settingsProfileImage -> {

                                val intent = Intent()
                                intent.type = "image/*"
                                intent.action = Intent.ACTION_GET_CONTENT
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                getProfileImageFromGallery.launch(intent)

                            }

                            rootView.settingsCoverImage -> {


                                val intent = Intent()
                                intent.type = "image/*"
                                intent.action = Intent.ACTION_GET_CONTENT
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                getCoverImageFromGallery.launch(intent)

                            }

                        }


                    }
                }
            })

        builderSingle.show()
    }

    private fun startBiggerImage(imageBundle: Bundle) {

        val biggerImageFragment = BiggerImageFragment.newInstance()
        val ft = parentFragmentManager.beginTransaction()
        biggerImageFragment.arguments = imageBundle
        ft.replace(rootView.fragmentContainerView.id, biggerImageFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.addToBackStack(null)
        ft.commit()

    }


    private val getProfileImageFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data

                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {

                    val source: ImageDecoder.Source =
                        ImageDecoder.createSource(
                            requireContext().contentResolver,
                            selectedImageUri
                        )
                    val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)

                    settingsViewModel.setProfileImage(bitmap)
                        .observe(viewLifecycleOwner, object : Observer<AuthState?> {
                            override fun onChanged(authState: AuthState?) {

                                when (authState) {

                                    is AuthState.Loading -> {
                                        Toast.makeText(
                                            context,
                                            "Uploading image ... ",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    is AuthState.Success -> {

                                        Toast.makeText(
                                            context,
                                            "Profile image changed successfully!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    is AuthState.AuthError -> {

                                        Toast.makeText(
                                            context,
                                            authState.message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

                                    else -> {}
                                }

                            }
                        })

                }
            }
        }

    private val getCoverImageFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data

                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {

                    val source: ImageDecoder.Source =
                        ImageDecoder.createSource(
                            requireContext().contentResolver,
                            selectedImageUri
                        )
                    val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)

                    settingsViewModel.setCoverImage(bitmap)
                        .observe(viewLifecycleOwner, object : Observer<AuthState?> {
                            override fun onChanged(authState: AuthState?) {
                                when (authState) {

                                    is AuthState.Loading -> {
                                        Toast.makeText(
                                            context,
                                            "Uploading image please be patient... ",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    is AuthState.Success -> {
                                        Log.e("Cover image", "Success ${authState.toString()}")
                                        Toast.makeText(
                                            context,
                                            "Cover image changed successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    is AuthState.AuthError -> {

                                        Toast.makeText(
                                            context,
                                            authState.message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

                                    else -> {}
                                }

                            }
                        })

                }
            }
        }


}
