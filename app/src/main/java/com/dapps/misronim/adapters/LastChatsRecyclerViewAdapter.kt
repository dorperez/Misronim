package com.dapps.misronim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dapps.misronim.databinding.LastChatsRowBinding
import com.dapps.misronim.model.User

class LastChatsRecyclerViewAdapter :
    RecyclerView.Adapter<LastChatsRecyclerViewAdapter.LastChatsHolder?>() {

    private lateinit var theUserList: List<HashMap<String, Any?>>
    private lateinit var listener: LastChatsRecyclerViewAdapter.OnClickedUser

    inner class LastChatsHolder(private val itemBinding: LastChatsRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindUsers(usersHashMap: HashMap<String, Any?>) {

            val user: User = usersHashMap["Sender"] as User
            val messages: Int = usersHashMap["Messages"] as Int

            if (user.status == "Online") {
                itemBinding.lastChatsImageOffline.visibility = View.GONE
                itemBinding.lastChatsImageOnline.visibility = View.VISIBLE
            }

            Glide.with(itemBinding.root).load(user.profilePic)
                .into(itemBinding.lastChatsUsersProfilePicture)


            itemBinding.lastChatsUsersUserName.text = user.userName

            when {
                messages == 0 -> {
                    itemBinding.lastChatsNumberOfNewMessages.visibility = View.GONE
                }
                messages == 1 -> {
                    itemBinding.lastChatsNumberOfNewMessages.text =
                        messages.toString() + " New Message"
                }
                messages > 1 -> {
                    itemBinding.lastChatsNumberOfNewMessages.text =
                        messages.toString() + " New Messages"
                }
            }

            itemBinding.root.setOnClickListener {
                listener.onClickedUser(user)
            }

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LastChatsRecyclerViewAdapter.LastChatsHolder {
        val rootView =
            LastChatsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LastChatsHolder(rootView)
    }

    override fun onBindViewHolder(
        holder: LastChatsRecyclerViewAdapter.LastChatsHolder,
        position: Int
    ) {
        holder.bindUsers(theUserList[position])
    }

    override fun getItemCount(): Int {
        return theUserList.size
    }

    fun getUserList(): List<HashMap<String, Any?>> {
        return theUserList
    }

    fun setUserList(newList: List<HashMap<String, Any?>>?) {

        if (newList != null) {

            // Remove Duplicates Users
            val hs = HashSet<HashMap<String, Any?>>()
            hs.addAll(newList)
            (newList as ArrayList<HashMap<String, Any?>>)
            newList.clear()
            newList.addAll(hs)


            val sortedList = newList.sortedByDescending { it["Messages"].toString().toInt() }

            theUserList = sortedList
            notifyDataSetChanged()
        }
    }

    interface OnClickedUser {
        fun onClickedUser(user: User)
    }

    fun setOnUserClickListener(listener: OnClickedUser) {
        this.listener = listener
    }


}
