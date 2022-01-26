package com.dapps.misronim.repository

import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.MutableLiveData
import com.dapps.misronim.model.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.logging.Handler
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.HashMap

class LoginRegisterRepository {

    //FireBase
    private val auth : FirebaseAuth = Firebase.auth
    private var refToUsers : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    private var currentUser : FirebaseUser? = auth.currentUser


    // Auth State
    private val _authState by lazy { MutableLiveData<AuthState>(AuthState.Idle) }
    private val authState: MutableLiveData<AuthState> = _authState

    fun checkIfUserConnected(): Boolean {
        if (currentUser != null) {
            return true
        }
        return false
    }

    private fun isEmailAddressValid(email: String): Boolean {
        var isEmailValid = false
        val strExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val inputStr: CharSequence = email
        val objPattern: Pattern = Pattern.compile(strExpression, Pattern.CASE_INSENSITIVE)
        val objMatcher: Matcher = objPattern.matcher(inputStr)
        if (objMatcher.matches()) {
            isEmailValid = true
        }
        return isEmailValid
    }

    fun registerUser(emailAddress: String, password: String) {

        _authState.value = AuthState.Loading

        if (!isEmailAddressValid(emailAddress)) {
            _authState.value = AuthState.AuthError("Invalid email")
            return

        } else if (password.isEmpty()) {
            _authState.value = AuthState.AuthError("Password field can't be empty")
            return

        } else if (password.length < 6) {
            _authState.value = AuthState.AuthError("Password must be over 6 digits")
            return
        }


        auth.createUserWithEmailAndPassword(
            emailAddress, password
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success

                    val userUID = auth.currentUser!!.uid
                    val userEmail = auth.currentUser!!.email.toString()

                    val strParts: List<String> = userEmail.split("@")
                    val userName = strParts[0].toString().substring(0, 1)
                        .uppercase(Locale.getDefault()) + strParts[0].toString().substring(1)
                        .lowercase(Locale.getDefault())

                    Log.e("UID","UID $userUID")

                    refToUsers = refToUsers.child(userUID)

                    val userHashMap = HashMap<String,Any>()
                    userHashMap["uid"] = userUID
                    userHashMap["userEmail"] = userEmail
                    userHashMap["userName"] = userName
                    userHashMap["profilePic"] = "https://firebasestorage.googleapis.com/v0/b/misronim-f7150.appspot.com/o/profile_picture_place_holder.jpg?alt=media&token=0740964d-1684-42e0-8bc0-548da80401c9"
                    userHashMap["coverPic"] = "https://firebasestorage.googleapis.com/v0/b/misronim-f7150.appspot.com/o/cover_image_place_holdedr.jpg?alt=media&token=72da7953-f1f4-4b95-bd25-3e29d9488bf9"
                    userHashMap["status"] = "Offline"
                    userHashMap["search"] = userEmail.lowercase(Locale.getDefault())
                    userHashMap["facebook"] = "https://www.facebook.com/"
                    userHashMap["instagram"] = "https://www.instagram.com/"
                    userHashMap["website"] = "https://www.google.com/"

                    refToUsers.updateChildren(userHashMap).addOnCompleteListener { childTask ->
                        if (childTask.isSuccessful){
                         Log.e("User","User Updated!")
                        }
                    }


                } else {
                    task.exception?.let {
                        _authState.value = AuthState.AuthError(it.localizedMessage)
                    }
                }
            }
    }

    fun loginUser(emailAddress: String, password: String) {

        _authState.value = AuthState.Loading

        if (!isEmailAddressValid(emailAddress)) {
            _authState.value = AuthState.AuthError("Invalid email")
            return

        } else if (password.isEmpty()) {
            _authState.value = AuthState.AuthError("Password field can't be empty")
            return

        } else if (emailAddress.isEmpty()) {
            _authState.value = AuthState.AuthError("Email field can't be empty")
            return

        }


        auth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                _authState.value = AuthState.Success
            } else {
                task.exception?.let {
                    _authState.value = AuthState.AuthError(it.localizedMessage)
                }
            }

        }
    }

    fun resetUserPassword(userEmailAddress: String) {

        _authState.value = AuthState.Loading

        if (userEmailAddress.isEmpty()) {
            _authState.value = AuthState.AuthError("Email address can't be empty")
            return
        }

        auth.sendPasswordResetEmail(userEmailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    task.exception.let {
                        _authState.value = AuthState.AuthError(it?.message)
                    }
                }
            }
    }

    fun getState(): MutableLiveData<AuthState> {
        return authState
    }


}