package com.dapps.misronim.notifications

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)


        val currentUser = FirebaseAuth.getInstance().currentUser
        val refreshToken = FirebaseMessaging.getInstance().token

        if (currentUser != null){
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: Task<String>) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val tokenRef = FirebaseDatabase.getInstance().reference.child("Tokens")

        refreshToken.addOnCompleteListener {
            val messageToken = Token(it.result)
            tokenRef.child(currentUser!!.uid).setValue(messageToken)
        }

    }


}