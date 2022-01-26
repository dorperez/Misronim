package com.dapps.misronim.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dapps.misronim.databinding.FragmentBiggerImageBinding


class BiggerImageFragment : Fragment() {

    lateinit var rootView: FragmentBiggerImageBinding

    companion object {

        fun newInstance() = BiggerImageFragment()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = FragmentBiggerImageBinding.inflate(LayoutInflater.from(context))

        val imageUrl = arguments?.getString("image")


        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(rootView.biggerImage)
        }


        rootView.biggerImage.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_MOVE) {
                    view?.performClick()
                    requireActivity().supportFragmentManager.popBackStack()
                }

                return true
            }
        })

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
            )


        return rootView.root
    }


}