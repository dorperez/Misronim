package com.dapps.misronim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dapps.misronim.model.ChatList
import com.dapps.misronim.model.User
import com.dapps.misronim.repository.MainActivityRepository

class ChatsListFragmentViewModel : ViewModel() {

    private val mainRepository : MainActivityRepository = MainActivityRepository()
    private var userData: MutableLiveData<User> = mainRepository.getCurrentUserInfo()
    val recentChatList = mainRepository.newMessages


    fun getUsersChatList(){
        mainRepository.getUsersChatList()
    }

    fun getUserInfo() : LiveData<User> {
        return userData
    }

    fun updateUserToken() {
        mainRepository.updateUserToken()
    }

}