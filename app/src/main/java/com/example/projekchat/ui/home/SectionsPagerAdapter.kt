package com.example.projekchat.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projekchat.R
import com.example.projekchat.ui.home.ui.chat.ChatFragment
import com.example.projekchat.ui.home.ui.friend.FriendFragment


private val TAB_TITLES = arrayOf(
    R.string.tab_title_chat,
    R.string.tab_title_friend
)

class SectionsPagerAdapter(activity: HomeActivity):FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment?=null
        when(position){
            0-> fragment = ChatFragment() as Fragment
            1-> fragment = FriendFragment() as Fragment
        }
        return fragment as Fragment
    }
}