package com.dapps.misronim.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.User
import com.dapps.misronim.repository.MainActivityRepository

class SettingsFragmentViewModel : ViewModel() {

    private val mainRepository: MainActivityRepository = MainActivityRepository()
    private val currentUser: LiveData<User> = mainRepository.getCurrentUserInfo()

    fun getCurrentUser(): LiveData<User> {
        return currentUser
    }

    fun setNewFacebook(newLink: String): MutableLiveData<AuthState> {
        mainRepository.setNewFacebookLink(newLink)
        return mainRepository.getState()
    }

    fun setNewInstagram(newLink: String): MutableLiveData<AuthState> {
        mainRepository.setNewInstagram(newLink)
        return mainRepository.getState()
    }

    fun setNewWebPage(newLink: String): MutableLiveData<AuthState> {
        mainRepository.setNewWebPage(newLink)
        return mainRepository.getState()
    }

    fun setProfileImage(bitmap: Bitmap): MutableLiveData<AuthState> {
        mainRepository.setProfileImage(bitmap)
        return mainRepository.getProfilePicState()
    }

    fun setCoverImage(bitmap: Bitmap): MutableLiveData<AuthState> {
        mainRepository.setCoverImage(bitmap)
        return mainRepository.getCoverPicState()
    }


}