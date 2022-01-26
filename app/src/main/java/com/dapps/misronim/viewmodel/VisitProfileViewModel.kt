package com.dapps.misronim.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dapps.misronim.model.User
import com.dapps.misronim.repository.MainActivityRepository

class VisitProfileViewModel : ViewModel() {

    private val mainRepository : MainActivityRepository = MainActivityRepository()

    fun getUserDetails(targetUID: String?): MutableLiveData<User> {
        return mainRepository.getUserByID(targetUID)
    }
}