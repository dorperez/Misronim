package com.dapps.misronim.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.Message
import com.dapps.misronim.model.User
import com.dapps.misronim.repository.MainActivityRepository

class ChatActivityViewModel : ViewModel() {

    private val mainRepository: MainActivityRepository = MainActivityRepository()
    private val userInfo: MutableLiveData<User> = mainRepository.getCurrentUserInfo()

    fun uploadImage(context: Context, uri: Uri, receiverUID: String): MutableLiveData<AuthState> {
        mainRepository.uploadChatImageToStorage(context, uri, receiverUID)
        return mainRepository.getState()
    }

    fun sendMessageToUser(
        context: Context,
        senderID: String?,
        receiverUID: String?,
        messageToSend: String
    ): MutableLiveData<AuthState> {
        mainRepository.sendMessageToUser(context, senderID, receiverUID, messageToSend)
        return mainRepository.getState()
    }

    fun retrieveMessages(senderUID: String?, receiverUID: String?): MutableLiveData<List<Message>> {
        return mainRepository.retrieveMessages(senderUID, receiverUID)
    }

    fun setSeenMessage(receiverUID: String) {
        mainRepository.setSeenMessage(receiverUID)
    }

    fun getChatDataFromNotification(userID: String): MutableLiveData<User> {
        return mainRepository.getUserByID(userID)
    }

    fun getChatDataFromNotificationState(): MutableLiveData<AuthState> {
        return mainRepository.getUserByIDState()
    }

    fun getCurrentUser(): LiveData<User> {
        return userInfo
    }


}