package com.dapps.misronim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dapps.misronim.databinding.SearchUsersRowBinding
import com.dapps.misronim.model.User
import java.util.*

class SearchUsersRecyclerViewAdapter :
    RecyclerView.Adapter<SearchUsersRecyclerViewAdapter.SearchHolder?>() {

    private lateinit var theUserList: List<User>
    private lateinit var listener: OnClickedUser

    inner class SearchHolder(private val itemBinding: SearchUsersRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindUsers(user: User) {

            if (user.status == "Online") {
                itemBinding.searchUsersImageOffline.visibility = View.GONE
                itemBinding.searchUsersImageOnline.visibility = View.VISIBLE
            }

            Glide.with(itemBinding.root).load(user.profilePic)
                .into(itemBinding.searchUsersProfilePicture)

            //Email Address To UserName
            val userEmail = user.userEmail
            val strParts: List<String>? = userEmail?.split("@")
            val userName = strParts?.get(0).toString().substring(0, 1)
                .uppercase(Locale.getDefault()) + strParts?.get(0).toString().substring(1)
                .lowercase(Locale.getDefault())

            itemBinding.searchUsersUserName.text = userName

            itemBinding.root.setOnClickListener {

                listener.onClickedUser(user)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val rootView =
            SearchUsersRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHolder(rootView)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bindUsers(theUserList[position])
    }

    override fun getItemCount(): Int {
        return theUserList.size
    }

    fun getUserList(): List<User> {
        return theUserList
    }

    fun setUserList(newList: List<User>?) {
        if (newList != null) {
            theUserList = newList
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