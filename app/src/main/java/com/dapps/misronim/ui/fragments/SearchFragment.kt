package com.dapps.misronim.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dapps.misronim.adapters.SearchUsersRecyclerViewAdapter
import com.dapps.misronim.databinding.FragmentSearchBinding
import com.dapps.misronim.model.User
import com.dapps.misronim.ui.ChatActivity
import com.dapps.misronim.ui.VisitProfileActivity
import com.dapps.misronim.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class SearchFragment : Fragment() {


    private lateinit var mainViewModel: MainViewModel
    private lateinit var rootView: FragmentSearchBinding
    private lateinit var adapter: SearchUsersRecyclerViewAdapter
    private lateinit var currentUser: LiveData<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        currentUser = mainViewModel.getUserInfo()

        trackBackButton()
        getAllUsers()

    }

    private fun trackBackButton() {

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if (rootView.searchFragmentSearchUsersEditText.isFocusable) {

                        rootView.searchFragmentSearchUsersEditText.text.clear()
                        rootView.searchFragmentSearchUsersEditText.clearFocus()
                        mainViewModel.getAllUsers()

                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

    }

    private fun trackEditTextChanges() {


        rootView.searchFragmentSearchUsersEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(beforeText: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun onTextChanged(textIsChanging: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Log.e("onTextChanged","Result: ${textIsChanging.toString()}")

                val textToSearch = textIsChanging.toString().lowercase(Locale.getDefault())

                if (textToSearch.isEmpty()) {
                    mainViewModel.getAllUsers()
                } else {
                    mainViewModel.searchForUser(textToSearch)
                        .observe(viewLifecycleOwner, object : Observer<List<User>?> {
                            override fun onChanged(newList: List<User>?) {

                                adapter.setUserList(newList)

                            }
                        })
                }


            }

            override fun afterTextChanged(afterText: Editable?) {

            }
        })

    }

    private fun getAllUsers() {

        mainViewModel.getAllUsers().observe(this, object : Observer<List<User>?> {
            override fun onChanged(newList: List<User>?) {

                updateRecyclerView(newList)

            }
        })

    }

    private fun updateRecyclerView(newList: List<User>?) {

        adapter = SearchUsersRecyclerViewAdapter()
        adapter.setUserList(newList)
        rootView.searchFragmentRecyclerView.adapter = adapter

        adapter.setOnUserClickListener(object : SearchUsersRecyclerViewAdapter.OnClickedUser {
            override fun onClickedUser(user: User) {

                val userClickedDialog =
                    MaterialAlertDialogBuilder(
                        requireContext(),
                        com.dapps.misronim.R.style.dialogStyle
                    )

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
                                    currentUser.value!!.profilePic
                                )
                                startChatIntent.putExtra("userProfilePic", user.profilePic)
                                startChatIntent.putExtra("userUID", user.uid)
                                startChatIntent.putExtra(
                                    "currentUserUID",
                                    mainViewModel.getUserInfo().value?.uid
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
        rootView = FragmentSearchBinding.inflate(LayoutInflater.from(context), container, false)


        rootView.searchFragmentRecyclerView.setHasFixedSize(true)
        rootView.searchFragmentRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        trackEditTextChanges()

        return rootView.root
    }

    companion object {

    }


}