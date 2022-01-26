package com.dapps.misronim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.User
import com.dapps.misronim.repository.MainActivityRepository

class MainViewModel : ViewModel() {

    private val mainRepository = MainActivityRepository()

    private var userData: MutableLiveData<User> = mainRepository.getCurrentUserInfo()
    private lateinit var searchForUser: MutableLiveData<List<User>>


    fun getUserInfo(): LiveData<User> {
        return userData
    }

    fun getLoadingState(): MutableLiveData<AuthState> {
        return mainRepository.getState()
    }

    fun getAllUsers(): MutableLiveData<List<User>> {
        return mainRepository.getAllUsers()
    }

    fun searchForUser(textIsChanging: String?): LiveData<List<User>> {
        searchForUser = mainRepository.searchForUsersInList(textIsChanging)
        return searchForUser

    }

    fun updateUserOnlineStatus(status: String) {
        mainRepository.updateUserOnlineStatus(status)
    }


}