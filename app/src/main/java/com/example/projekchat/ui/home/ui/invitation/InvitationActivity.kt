package com.example.projekchat.ui.home.ui.invitation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvitationActivity : AppCompatActivity(), InvitationAdapter.OnItemClickCallback {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText:TextView
    private lateinit var loading:ProgressBar

    private lateinit var viewModel: InvitationViewModel
    private var listUserResponse = ArrayList<UserResponse>()
    private var mapUser = HashMap<String, UserResponse>()
    private var adapter = InvitationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitation)
        supportActionBar?.title = "Invitation"
        initializationLayout()

        loading.visibility = View.VISIBLE
        emptyText.visibility = View.INVISIBLE

        viewModel.mapUser.observe(this, Observer {
            mapUser = it
        })

        observeListInvitation()

//        GlobalScope.launch {
//            withContext(Dispatchers.Main){
//                loading.visibility = View.VISIBLE
//                emptyText.visibility = View.INVISIBLE
//                if (getAllInvitation().isEmpty()){
//                    loading.visibility = View.INVISIBLE
//                    emptyText.visibility =View.VISIBLE
//                }else{
//                    loading.visibility = View.INVISIBLE
//                    emptyText.visibility = View.INVISIBLE
//                }
//                setAdapter()
//            }
//        }
    }

    private fun observeListInvitation(){
        viewModel.listInvitation.observe(this, Observer {
            listUserResponse.clear()
            listUserResponse.addAll(it)
            if (listUserResponse.isEmpty()){
                loading.visibility = View.INVISIBLE
                emptyText.visibility =View.VISIBLE
            }else{
                loading.visibility = View.INVISIBLE
                emptyText.visibility = View.INVISIBLE
            }
            setAdapter()
        })
    }

    private fun setAdapter(){
        adapter.setListUser(listUserResponse)
        adapter.setOnItemClickCallback(this)
        recyclerView.adapter = adapter

//        adapter.setOnItemClickCallback(object :InvitationAdapter.OnItemClickCallback{
//            @SuppressLint("WrongConstant")
//            override fun onItemClicked(user: String, position: Int) {
//                GlobalScope.launch {
//                    FirestoreService().setFriend("${getCurrentUser().toString()}", "${user.toString()}")
//                    var result = FirestoreService().removeInvitation("${getCurrentUser().toString()}", "${user}")
//                    withContext(Dispatchers.Main){
//                        if (result==FirestoreService.SUCCESS){
//                            var list = getAllInvitation()
//                            adapter.setListUser(list)
//                            recyclerView.adapter = adapter
//                            Snackbar.make(this@InvitationActivity.recyclerView, "ADDED TO FRIEND", Snackbar.ANIMATION_MODE_SLIDE).show()
//                        }
//                    }
//                }
//            }
//
//        })
    }

    private fun initializationLayout() {
        recyclerView = findViewById(R.id.invitationActivity_recycleView)
        emptyText = findViewById(R.id.invitationActivity_empty)
        loading = findViewById(R.id.invitationActivity_loading)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[InvitationViewModel::class.java]
    }

    private fun getCurrentUser(): String? {
        var authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

    @SuppressLint("WrongConstant")
    override fun onItemClicked(userResponseFriend: UserResponse) {
        val userResponse = mapUser["${getCurrentUser()}"]
        if (userResponse!=null&&userResponseFriend!=null){
            GlobalScope.launch {
                FirestoreService().setFriend(userResponse, userResponseFriend)
                var result = FirestoreService().removeInvitation("${userResponse.email}", "${userResponseFriend.email}")
                withContext(Dispatchers.Main){
                    if (result==FirestoreService.SUCCESS){
                        adapter.notifyDataSetChanged()
                        Snackbar.make(this@InvitationActivity.recyclerView, "ADDED TO FRIEND", Snackbar.ANIMATION_MODE_SLIDE).show()
                    }
                }
            }
        }
    }


}