package com.example.projekchat.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.projekchat.R
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.ui.home.ui.invitation.InvitationActivity
import com.example.projekchat.ui.home.ui.searchfriend.SearchFriendActivity
import com.example.projekchat.ui.login.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

//PENAMBAHAN PADA HOME ACTIVITY PADA SISI REMOTE GITHUB

class HomeActivity : AppCompatActivity() {

    private lateinit var btn_searchFriend:FloatingActionButton

    private val TAB_TITLES = arrayOf(
        "CHAT",
        "FRIEND"
    )

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_invitation->{
                val intent = Intent(this, InvitationActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout->{
                doLogout()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun doLogout() {
        val authenticationService = AuthenticationService()
        authenticationService.logOutUser()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.elevation = 0f

        initializationLayout()

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager:ViewPager2 = findViewById(R.id.mainActivity_viewPager)
        viewPager.adapter = sectionsPagerAdapter

        val tabs:TabLayout = findViewById(R.id.mainActivity_tabLayout)

        TabLayoutMediator(tabs, viewPager){
            tabs, position -> tabs.text = TAB_TITLES[position]
        }.attach()

        btn_searchFriend.setOnClickListener {
            val intent = Intent(this, SearchFriendActivity::class.java)
            startActivity(intent)
        }

    }
    
    //INI HAL YANG GUA UBAH

    private fun initializationLayout() {
        btn_searchFriend = findViewById(R.id.mainActivity_btn_searchFriend)
    }
}
