package com.dapps.misronim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.repository.LoginRegisterRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginRegisterViewModel : ViewModel() {

    private val repository = LoginRegisterRepository()
    private val loadingState : MutableLiveData<AuthState> = repository.getState()


    fun registerUser(userEmail : String,userPassword : String){
        repository.registerUser(userEmail,userPassword)
    }

    fun loginUser(userEmail : String,userPassword : String){
        repository.loginUser(userEmail,userPassword)
    }

    fun checkIfUserConnected() : Boolean{
        return repository.checkIfUserConnected()
    }

    fun getAuthState() : MutableLiveData<AuthState>{
        return loadingState
    }

    fun resetUserPassword(userEmailAddress: String) {
        return repository.resetUserPassword(userEmailAddress)
    }


}