package com.dapps.misronim.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dapps.misronim.databinding.ActivityVisitProfileBinding
import com.dapps.misronim.model.User
import com.dapps.misronim.ui.fragments.BiggerImageFragment
import com.dapps.misronim.viewmodel.VisitProfileViewModel

class VisitProfileActivity : AppCompatActivity() {

    private lateinit var visitProfileViewModel: VisitProfileViewModel
    private lateinit var visitProfileBinding: ActivityVisitProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        visitProfileBinding = ActivityVisitProfileBinding.inflate(layoutInflater)
        setContentView(visitProfileBinding.root)

        visitProfileViewModel = ViewModelProvider(this)[VisitProfileViewModel::class.java]

        setToolBar()

        intent = intent
        val targetUID = intent.getStringExtra("targetUID").toString()
        getUserDetails(targetUID)

    }

    private fun setToolBar() {
        setSupportActionBar(visitProfileBinding.visitProfileToolBar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        visitProfileBinding.visitProfileToolBar.setNavigationOnClickListener(object :
            View.OnClickListener {
            override fun onClick(view: View?) {
                finish()
            }
        })
    }

    private fun getUserDetails(targetUID: String) {

        visitProfileViewModel.getUserDetails(targetUID).observe(this, object : Observer<User?> {
            override fun onChanged(userDetails: User?) {

                updateViews(userDetails)

            }
        })
    }

    private fun updateViews(userDetails: User?) {

        visitProfileBinding.visitProfileUserName.text = userDetails!!.userName

        Log.e("userDetails", "Cover ${userDetails.coverPic}")

        Glide.with(this).load(userDetails.profilePic)
            .into(visitProfileBinding.visitProfileProfileImage)
        Glide.with(this).load(userDetails.coverPic).into(visitProfileBinding.visitProfileCoverImage)

        visitProfileBinding.visitProfileFacebookButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.facebook))
            startActivity(browserIntent)
        }
        visitProfileBinding.visitProfileInstagramButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.instagram))
            startActivity(browserIntent)
        }
        visitProfileBinding.visitProfileWebButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.website))
            startActivity(browserIntent)
        }

        visitProfileBinding.visitProfileProfileImage.setOnClickListener {

            val imageBundle = Bundle()
            imageBundle.putString("image", userDetails.profilePic)
            startBiggerImage(imageBundle)

        }
        visitProfileBinding.visitProfileCoverImage.setOnClickListener {

            val imageBundle = Bundle()
            imageBundle.putString("image", userDetails.coverPic)
            startBiggerImage(imageBundle)

        }

    }

    private fun startBiggerImage(imageBundle: Bundle) {

        val biggerImageFragment = BiggerImageFragment.newInstance()
        val ft = supportFragmentManager.beginTransaction()
        biggerImageFragment.arguments = imageBundle
        ft.replace(visitProfileBinding.visitProfileContainerView.id, biggerImageFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.addToBackStack(null)
        ft.commit()

    }

}