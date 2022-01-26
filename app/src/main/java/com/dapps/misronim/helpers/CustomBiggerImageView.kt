package com.dapps.misronim.helpers

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class CustomBiggerImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        doSomething()
        return true
    }

    private fun doSomething() {

    }
}