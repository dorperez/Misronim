package com.dapps.misronim.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dapps.misronim.R
import com.dapps.misronim.adapters.LastChatsRecyclerViewAdapter
import com.dapps.misronim.databinding.FragmentChatsBinding
import com.dapps.misronim.model.User
import com.dapps.misronim.ui.ChatActivity
import com.dapps.misronim.ui.VisitProfileActivity
import com.dapps.misronim.viewmodel.ChatsListFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ChatsFragment : Fragment() {

    private lateinit var chatsBinding: FragmentChatsBinding
    private lateinit var chatsListFragmentViewModel: ChatsListFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatsListFragmentViewModel = ViewModelProvider(this)[ChatsListFragmentViewModel::class.java]

    }

    private fun updateRecyclerView(newList: List<HashMap<String, Any?>>?) {

        val adapter = LastChatsRecyclerViewAdapter()
        chatsBinding.chatListRecyclerView.adapter = adapter

        if (newList?.isEmpty() == true) {
            chatsBinding.emptyRecentChatTextView.visibility = View.VISIBLE
        } else {
            chatsBinding.emptyRecentChatTextView.visibility = View.GONE
        }

        adapter.setUserList(newList)

        val currentUserInfo = chatsListFragmentViewModel.getUserInfo()

        adapter.setOnUserClickListener(object : LastChatsRecyclerViewAdapter.OnClickedUser {
            override fun onClickedUser(user: User) {

                val userClickedDialog =
                    MaterialAlertDialogBuilder(requireContext(), R.style.dialogStyle)

                val arrayAdapter =
                    ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
                arrayAdapter.add("Send Message")
                arrayAdapter.add("View Profile")

                userClickedDialog.setAdapter(arrayAdapter,
                    DialogInterface.OnClickListener { dialog, which ->

                        when (which) {
                            // Send Message
                            0 -> {

                                val startChatIntent = Intent(context, ChatActivity::class.java)
                                startChatIntent.putExtra("userName", user.userName)
                                startChatIntent.putExtra(
                                    "currentUserProfilePic",
                                    currentUserInfo.value!!.profilePic
                                )
                                startChatIntent.putExtra("userProfilePic", user.profilePic)
                                startChatIntent.putExtra("userUID", user.uid)
                                startChatIntent.putExtra(
                                    "currentUserUID",
                                    currentUserInfo.value!!.uid
                                )
                                startActivity(startChatIntent)


                            }

                            // View Profile
                            1 -> {

                                val visitProfileIntent =
                                    Intent(context, VisitProfileActivity::class.java)
                                visitProfileIntent.putExtra("targetUID", user.uid)
                                startActivity(visitProfileIntent)

                            }


                        }
                    })

                userClickedDialog.show()

            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        chatsBinding = FragmentChatsBinding.inflate(LayoutInflater.from(context), container, false)


        //RecyclerView
        chatsBinding.chatListRecyclerView.setHasFixedSize(true)
        chatsBinding.chatListRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        updateEmptyRecyclerView()
        getLastUsersChatList()
        updateUserToken()



        return chatsBinding.root
    }

    private fun updateEmptyRecyclerView() {

        val zeroList = ArrayList<HashMap<String, Any?>>()
        updateRecyclerView(zeroList)

    }

    private fun updateUserToken() {
        chatsListFragmentViewModel.updateUserToken()
    }

    private fun getLastUsersChatList() {

        chatsListFragmentViewModel.getUsersChatList()

        chatsListFragmentViewModel.recentChatList.observe(viewLifecycleOwner,
            object : Observer<List<HashMap<String, Any?>>?> {
                override fun onChanged(newList: List<HashMap<String, Any?>>?) {

                    updateRecyclerView(newList)
                    Log.e("recentChatList", "Updated")
                }
            })


    }


}