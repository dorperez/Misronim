package com.dapps.misronim.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dapps.misronim.databinding.ActivityFullImageBinding

class FullImageActivity : AppCompatActivity() {

    lateinit var fullImageViewBinding: ActivityFullImageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullImageViewBinding = ActivityFullImageBinding.inflate(layoutInflater)


        setContentView(fullImageViewBinding.root)

        val imageUrl = intent.getStringExtra("imageUrl")

        Glide.with(fullImageViewBinding.root).load(imageUrl)
            .into(fullImageViewBinding.fullImageImageView)

    }


}