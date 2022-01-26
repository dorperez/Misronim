package com.dapps.misronim.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dapps.misronim.ui.fragments.ChatsFragment
import com.dapps.misronim.ui.fragments.SearchFragment
import com.dapps.misronim.ui.fragments.SettingsFragment


class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    private val fragmentsArray: Array<Fragment> = arrayOf(
        ChatsFragment(),
        SearchFragment(),
        SettingsFragment()
    )

    private val fragmentsTitleArray: ArrayList<String> = arrayListOf(
        "Chats",
        "Search",
        "Settings"
    )

    override fun getItemCount(): Int {
        return fragmentsArray.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsArray[position]
    }

    fun getPageTitle(position: Int): String {
        return fragmentsTitleArray[position]
    }


}