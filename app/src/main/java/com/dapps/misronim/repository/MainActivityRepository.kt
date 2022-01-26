package com.dapps.misronim.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.ChatList
import com.dapps.misronim.model.Message
import com.dapps.misronim.model.User
import com.dapps.misronim.notifications.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private const val Legacy_SERVER_KEY: String =
    "AAAAfVabI-8:APA91bH_P7lMBdgf3E1-MEYwDtahxpssP9A-5QRrSFLawAoEffrEUGOA5xXwDZIadB4SQfwLdYbhcllViIeVQBsevbTMnyrIa5ovbnFHtFgGazNqgbwtcTIqNW5n2VmZdC0zzVavOpAB"

private const val httpURL = "https://fcm.googleapis.com/fcm/send"

class MainActivityRepository {

    //FireBase
    private val auth: FirebaseAuth = Firebase.auth
    private var currentUser: FirebaseUser? = auth.currentUser

    //References
    private var currentUserRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!.uid)

    private var usersRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Users")

    private var mainRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference

    private var storageRef =
        FirebaseStorage.getInstance().reference.child("UsersImages")

    private var chatImagesRef =
        FirebaseStorage.getInstance().reference.child("ChatImages")

    private var chatRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Chat")

    private var chatListRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("ChatList")

    private var tokenRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Tokens")

    // Auth States
    //Main
    private val _authState by lazy { MutableLiveData<AuthState>(AuthState.Idle) }
    private val authState: MutableLiveData<AuthState> = _authState

    //Cover Pic State
    private val _coverPicState by lazy { MutableLiveData<AuthState>(AuthState.Idle) }
    private val coverPicState: MutableLiveData<AuthState> = _coverPicState

    //Profile Pic State
    private val _profilePicState by lazy { MutableLiveData<AuthState>(AuthState.Idle) }
    private val profilePicState: MutableLiveData<AuthState> = _profilePicState

    //getUserByID State
    private val _getUserByIDState by lazy { MutableLiveData<AuthState>(AuthState.Idle) }
    private val getUserByIDState: MutableLiveData<AuthState> = _getUserByIDState

    //Lists
    private var userList = MutableLiveData<List<User>>()
    private var messageList = MutableLiveData<List<Message>>()

    //MutableLiveData
    private var receiverID = MutableLiveData<String>()
    private val userData = MutableLiveData<User>()
    private val getUserByID = MutableLiveData<User>()


    //Last Messages
    var newMessages = MutableLiveData<List<HashMap<String, Any?>>>()
    private val hashMapArray = ArrayList<HashMap<String, Any?>>()

    //Variables
    var notificationNotify = false

    //FCM Notification API Ref
    //var apiService : ApiService = ApiClient.getClient("https://fcm.googleapis.com/")!!.create(ApiService::class.java)


    fun getCurrentUserInfo(): MutableLiveData<User> {

        _authState.value = AuthState.Loading

        currentUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                userData.value = snapshot.getValue(User::class.java)


                _authState.value = AuthState.Success


            }

            override fun onCancelled(error: DatabaseError) {
                _authState.value = AuthState.AuthError(error.message)
            }
        })

        return userData

    }

    fun getState(): MutableLiveData<AuthState> {
        return authState
    }

    fun getCoverPicState(): MutableLiveData<AuthState> {
        return coverPicState
    }

    fun getProfilePicState(): MutableLiveData<AuthState> {
        return profilePicState
    }

    fun getUserByIDState(): MutableLiveData<AuthState> {
        return getUserByIDState
    }

    fun getAllUsers(): MutableLiveData<List<User>> {

        val arrayList = ArrayList<User>()


        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                for (snapshot2 in snapshot.children) {

                    val user: User? = snapshot2.getValue(User::class.java)

                    if (user != null) {
                        if (!getCurrentUserInfo().value?.uid.equals(user.uid)) {
                            arrayList.add(user)
                        }
                    }
                }

                userList.value = arrayList

                _authState.value = AuthState.Success

            }

            override fun onCancelled(error: DatabaseError) {
                _authState.value = AuthState.AuthError(error.message)
            }
        })


        return userList

    }

    fun searchForUsersInList(textIsChanging: String?): MutableLiveData<List<User>> {

        val queryUser =
            usersRef.orderByChild("search").startAt(textIsChanging).endAt(textIsChanging + "\uf8ff")

        queryUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (ds in snapshot.children) {

                    val userObject = ds.getValue(User::class.java)

                    if (userObject != null) {
                        userList.value = listOf(userObject)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return userList
    }

    fun setNewFacebookLink(newLink: String) {

        val result: HashMap<String, Any> = HashMap()
        result["facebook"] = newLink

        currentUserRef.updateChildren(result).addOnCompleteListener {
            _authState.value = AuthState.Success
        }.addOnFailureListener {
            _authState.value = AuthState.AuthError(it.message.toString())
        }


    }

    fun setNewInstagram(newLink: String) {

        val result: HashMap<String, Any> = HashMap()
        result["instagram"] = newLink

        currentUserRef.updateChildren(result).addOnCompleteListener {
            _authState.value = AuthState.Success
        }.addOnFailureListener {
            _authState.value = AuthState.AuthError(it.message.toString())
        }

    }

    fun setNewWebPage(newLink: String) {

        val result: HashMap<String, Any> = HashMap()
        result["website"] = newLink

        currentUserRef.updateChildren(result).addOnCompleteListener {
            _authState.value = AuthState.Success
        }.addOnFailureListener {
            _authState.value = AuthState.AuthError(it.message.toString())
        }

    }

    fun setProfileImage(bitmap: Bitmap) {

        _profilePicState.value = AuthState.Loading


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val data: ByteArray = baos.toByteArray()

        val uniqueUID: String = UUID.randomUUID().toString()
        val riversRef = storageRef.child(uniqueUID)
        val uploadTask = riversRef.putBytes(data)


        uploadTask.addOnFailureListener {

            _profilePicState.value = AuthState.AuthError(it.message)

        }.addOnSuccessListener { taskSnapshot ->

            taskSnapshot.storage.downloadUrl.addOnCompleteListener {

                val imageDownloadURL = it.result.toString()
                val result: HashMap<String, Any> = HashMap()
                result["profilePic"] = imageDownloadURL

                currentUserRef.updateChildren(result).addOnCompleteListener {
                    _profilePicState.value = AuthState.Success
                }.addOnFailureListener {
                    _profilePicState.value = AuthState.AuthError(it.message)
                }
            }


        }

    }

    fun setCoverImage(bitmap: Bitmap) {

        _coverPicState.value = AuthState.Loading


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val data: ByteArray = baos.toByteArray()

        val uniqueUID: String = UUID.randomUUID().toString()
        val riversRef = storageRef.child(uniqueUID)
        val uploadTask = riversRef.putBytes(data)


        uploadTask.addOnFailureListener {

            _coverPicState.value = AuthState.AuthError(it.message)

        }.addOnSuccessListener { taskSnapshot ->

            taskSnapshot.storage.downloadUrl.addOnCompleteListener { it1 ->

                val imageDownloadURL = it1.result.toString()
                val result: HashMap<String, Any> = HashMap()
                result["coverPic"] = imageDownloadURL

                currentUserRef.updateChildren(result).addOnCompleteListener {
                    _coverPicState.value = AuthState.Success
                }.addOnFailureListener { it ->
                    _coverPicState.value = AuthState.AuthError(it.message)
                }
            }


        }

    }

    fun sendMessageToUser(
        context: Context,
        senderID: String?,
        receiverUID: String?,
        messageToSend: String
    ) {

        notificationNotify = true
        this.receiverID.value = receiverUID

        val messageKey = mainRef.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderID
        messageHashMap["message"] = messageToSend
        messageHashMap["receiver"] = receiverUID
        messageHashMap["seen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        mainRef.child("Chat").child(messageKey!!).setValue(messageHashMap).addOnCompleteListener {

            if (it.isSuccessful) {

                val chatListSenderRefrence =
                    chatListRef.child(currentUser?.uid!!).child(receiverUID!!)

                chatListSenderRefrence.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            chatListSenderRefrence.child("id").setValue(receiverUID)
                        }

                        Log.e("sendMessageToUser", "receiverID $receiverUID")
                        Log.e("sendMessageToUser", "CurrentUser ${currentUser?.uid!!}")

                        val chatListReceiverRefrence =
                            chatListRef.child(receiverUID).child(currentUser?.uid!!)
                        chatListReceiverRefrence.child("id").setValue(currentUser?.uid!!)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }

        val currentUserObject = getCurrentUserInfo()

        usersRef.child(receiverUID!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val userObject = snapshot.getValue(User::class.java)

                if (userObject!!.status == "Offline") {
                    sendNotification(
                        context, receiverUID, currentUserObject.value!!.uid,
                        currentUserObject.value!!.userName, messageToSend,
                        currentUserObject.value!!.profilePic
                    )
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    private fun sendNotification(
        context: Context,
        receiverID: String?,
        senderUID: String?,
        userName: String?,
        messageToSend: String,
        senderImage: Any?,
    ) {


        val queryUser = tokenRef.orderByKey().equalTo(receiverID)

        queryUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapShotChild in snapshot.children) {

                    for (snapShotChild2 in snapshot.children) {

                        val token: String = snapShotChild2.child("token").value.toString()


                        Log.e(
                            "notificationData: ",
                            "Data: ${currentUser!!.uid} + $userName + $messageToSend + $receiverID $senderImage"
                        )


//                        val defaultNotification = JSONObject()
//                        defaultNotification.put("body", messageToSend)
//                        defaultNotification.put("title", userName)
//                        defaultNotification.put("sound", "default")
//                        defaultNotification.put(
//                            "icon",
//                            R.drawable.camera_icon
//                        ) //   icon_name image must be there in drawable
//                        defaultNotification.put("image", userObject!!.profilePic)
//
//                        defaultNotification.put("tag", token)
//                        defaultNotification.put("priority", "high")

                        val mainObject = JSONObject()
                        val dataObject = JSONObject()
                        dataObject.put("message", messageToSend)
                        dataObject.put("title", userName)
                        dataObject.put("profileImage", senderImage)
                        dataObject.put("senderUID", senderUID)


                        mainObject.put("to", token)

                        //obj.put("notification", objData)
                        mainObject.put("data", dataObject)

                        val jsObjRequest: JsonObjectRequest =
                            object : JsonObjectRequest(
                                Request.Method.POST, httpURL, mainObject,
                                object : com.android.volley.Response.Listener<JSONObject?> {
                                    override fun onResponse(response: JSONObject?) {
                                        Log.e("Notification Message Sent", response.toString() + "")
                                    }
                                },
                                object : com.android.volley.Response.ErrorListener {
                                    override fun onErrorResponse(error: VolleyError) {
                                        Log.e("Notification Message Error:", error.toString() + "")
                                    }
                                }) {
                                @Throws(AuthFailureError::class)
                                override fun getHeaders(): Map<String, String> {
                                    val params: MutableMap<String, String> = HashMap()
                                    params["Authorization"] = "key=$Legacy_SERVER_KEY"
                                    params["Content-Type"] = "application/json"
                                    return params
                                }
                            }

                        val requestQueue = Volley.newRequestQueue(context)
                        val socketTimeout = 1000 * 60 // 60 seconds
                        val policy: RetryPolicy = DefaultRetryPolicy(
                            socketTimeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                        jsObjRequest.retryPolicy = policy
                        requestQueue.add(jsObjRequest)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("VolleyRequest", "Cancel")
            }
        })
    }

    fun uploadChatImageToStorage(context: Context, uri: Uri, receiverUID: String) {

        notificationNotify = true

        Log.e("TakeImage", "Working.. $uri ")
        _authState.value = AuthState.Loading

        val messageID = mainRef.push().key
        val filePath = chatImagesRef.child("$messageID.png")
        val uploadTask = filePath.putFile(uri)


        uploadTask.addOnFailureListener {

            Log.e("TakeImage", "Fail: ${it.message}")
            _authState.value = AuthState.AuthError(it.message)

        }.addOnSuccessListener { taskSnapshot ->
            Log.e("TakeImage", "Ok,1 more step")
            taskSnapshot.storage.downloadUrl.addOnCompleteListener {

                val imageDownloadURL = it.result.toString()
                Log.e("TakeImage", "Sucess: $imageDownloadURL")
                val messageHashMap = HashMap<String, Any?>()
                messageHashMap["sender"] = currentUser!!.uid
                messageHashMap["message"] = "Sent you an image"
                messageHashMap["receiver"] = receiverUID
                messageHashMap["seen"] = false
                messageHashMap["url"] = imageDownloadURL
                messageHashMap["messageId"] = messageID

                val currentUserObject = getCurrentUserInfo()

                mainRef.child("Chat").child(messageID!!).setValue(messageHashMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            usersRef.child(receiverUID)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        val userObject = snapshot.getValue(User::class.java)

                                        if (userObject!!.status == "Offline") {
                                            sendNotification(
                                                context,
                                                receiverUID,
                                                currentUserObject.value!!.uid,
                                                currentUserObject.value!!.userName,
                                                "Sent you an image",
                                                currentUserObject.value!!.profilePic
                                            )
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })

                        }
                    }

            }


        }

    }

    fun retrieveMessages(senderUID: String?, receiverUID: String?): MutableLiveData<List<Message>> {

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val arrayList = ArrayList<Message>()

                for (snapshot2 in snapshot.children) {

                    val chat = snapshot2.getValue(Message::class.java)

                    if (chat!!.receiver.equals(senderUID) && chat.sender.equals(receiverUID) ||
                        chat.receiver.equals(receiverUID) && chat.sender.equals(senderUID)
                    ) {

                        arrayList.add(chat)
                    }

                }

                messageList.value = arrayList

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return messageList
    }

    fun getUsersChatList() {

        chatListRef.child(currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val arrayList = ArrayList<ChatList>()

                for (snapshot2 in snapshot.children) {

                    val singleListItem = snapshot2.getValue(ChatList::class.java)

                    if (singleListItem != null) {
                        arrayList.add(singleListItem)
                    }

                }

                getUsersLastChat(arrayList)


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun getUsersLastChat(arrayList: ArrayList<ChatList>) {

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapshot2 in snapshot.children) {

                    val user = snapshot2.getValue(User::class.java)

                    for (eachChatList in arrayList) {

                        if (user!!.uid.equals(eachChatList.id)) {
                            searchNumberOfMessages(user)
                        }

                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    fun searchNumberOfMessages(user: User) {

        val hasMap = HashMap<String, Any?>()

        hasMap.clear()
        hashMapArray.clear()

        if (newMessages.value != null) {
            (newMessages.value as ArrayList<HashMap<String, Any?>>).clear()
        }

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val messageArray = ArrayList<String?>()

                for (snapshot2 in snapshot.children) {

                    val chat = snapshot2.getValue(Message::class.java)

                    if (chat!!.receiver.equals(currentUser!!.uid) && chat.sender.equals(
                            user.uid
                        )
                    ) {

                        if (chat.seen == false) {

                            messageArray.add(chat.message)

                        }

                    }

                }

                hasMap["Sender"] = user
                hasMap["Messages"] = messageArray.size
                hashMapArray.add(hasMap)
                newMessages.value = hashMapArray

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    fun setSeenMessage(receiverUID: String) {

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Message::class.java)

                    if (chat!!.receiver.equals(currentUser!!.uid) && chat.sender.equals(receiverUID)) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["seen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    fun getUserByID(userUID: String?): MutableLiveData<User> {

        _getUserByIDState.value = AuthState.Loading

        usersRef.child(userUID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("2", "dataSnapshot $snapshot")
                val userObject = snapshot.getValue(User::class.java)
                getUserByID.value = userObject!!
                _getUserByIDState.value = AuthState.Success

            }

            override fun onCancelled(error: DatabaseError) {
                _getUserByIDState.value = AuthState.AuthError(error.message)
            }
        })

        return getUserByID

    }

    fun updateUserToken() {

        val userToken = FirebaseMessaging.getInstance().token

        userToken.addOnCompleteListener {
            val token = Token(userToken.result)
            tokenRef.child(currentUser!!.uid).setValue(token)
        }

    }

    fun updateUserOnlineStatus(status: String) {

        val statusHashMap = HashMap<String, Any?>()
        statusHashMap["status"] = status
        currentUserRef.updateChildren(statusHashMap)

    }


}