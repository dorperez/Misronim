package com.dapps.misronim.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.dapps.misronim.R
import com.dapps.misronim.adapters.MainViewPagerAdapter
import com.dapps.misronim.databinding.ActivityMainBinding
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.model.User
import com.dapps.misronim.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private var chatRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Chat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolBar)

        inflateToolBarInfo()
        inflateViewPager()
        observeLoadingState()
        updateUserOnlineStatus()

    }

    private fun updateUserOnlineStatus() {

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                mainViewModel.updateUserOnlineStatus("Online")
            }

            override fun onPause(owner: LifecycleOwner) {
                mainViewModel.updateUserOnlineStatus("Offline")
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                mainViewModel.updateUserOnlineStatus("Offline")
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                mainViewModel.updateUserOnlineStatus("Online")
            }

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                mainViewModel.updateUserOnlineStatus("Online")
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                mainViewModel.updateUserOnlineStatus("Offline")
            }
        })

    }


    private fun observeLoadingState() {

        mainViewModel.getLoadingState().observe(this, object : Observer<AuthState?> {
            override fun onChanged(authState: AuthState?) {

                when (authState) {

                    is AuthState.Loading -> {
                        binding.mainActivityLoadingLayout.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.mainActivityLoadingLayout.visibility = View.GONE
                    }

                }

            }
        })
    }

    private fun inflateToolBarInfo() {

        mainViewModel.getUserInfo().observe(this, object : Observer<User?> {
            override fun onChanged(userData: User?) {

                if (userData != null) {

                    binding.toolbarUserEmailTextView.text = userData.userName
                    Glide.with(this@MainActivity).load(userData.profilePic)
                        .into(binding.toolbarUserImage)

                }

            }
        })

    }

    private fun inflateViewPager() {

        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.mainViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_logout) {

            MaterialAlertDialogBuilder(this, R.style.dialogStyle)
                .setTitle("Logout")
                .setIcon(R.drawable.logout_button)
                .setMessage("Are you sure you want to logout ?")
                .setNegativeButton("Logout", DialogInterface.OnClickListener { dialogInterface, i ->

                    mainViewModel.updateUserOnlineStatus("Offline")

                    Firebase.auth.signOut()
                    val intent = Intent(this@MainActivity, LoginRegisterActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                })
                .setPositiveButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .show()
        }

        return super.onOptionsItemSelected(item)
    }

}