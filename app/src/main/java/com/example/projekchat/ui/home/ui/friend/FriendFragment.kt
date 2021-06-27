package com.example.projekchat.ui.home.ui.friend

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.projekchat.ui.home.ui.chatlog.RoomChatActivity
import com.example.projekchat.utils.OnItemClickCallback
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FriendFragment : Fragment(), OnItemClickCallback {

    private lateinit var emptyText:TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading:ProgressBar

    private lateinit var viewModel:FriendViewModel

    private lateinit var userResponseUser:UserResponse
    private var mapAllUser = HashMap<String, UserResponse>()

    private var adapter = FriendAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization(view)

        viewModel.mapAllUser.observe(viewLifecycleOwner, Observer {
            mapAllUser = it
        })

        observeListFriend()
    }

    private fun observeListFriend(){
        viewModel.listfriend.observe(viewLifecycleOwner, Observer {
            loading.visibility=View.INVISIBLE
            if (it.isEmpty()){
                emptyText.visibility=View.VISIBLE
            }
            adapter.setListUser(it)
            recyclerView.adapter = adapter
            adapter.setOnItemClickCallback(this)
        })
    }

    private fun initialization(view: View) {
        recyclerView = view.findViewById(R.id.friendFragment_recycleview)
        emptyText = view.findViewById(R.id.friendFragment_empty)
        loading = view.findViewById(R.id.friendFragment_loading)

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(FriendViewModel::class.java)
    }

    private fun getCurrentUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

    override fun onItemLastMessageClicked(userResponse: UserResponse, userFriendResponse: UserResponse) {
//        TODO("Not yet implemented")
    }

    override fun onItemFriendClicked(user: UserResponse) {
        if (user!=null && mapAllUser.isNotEmpty()){
            var userResponseFriend = mapAllUser["${user.email}"]
            var userResponseUser = mapAllUser["${getCurrentUser()}"]
//            println(userResponseFriend.toString())
//            println(userResponseUser.toString())
            GlobalScope.launch {
                val firestoreService = FirestoreService().MessageService()
                firestoreService.createChatRoom(userResponseUser?.email!!, userResponseFriend?.email!!)
            }
            val intent = Intent(this.context, RoomChatActivity::class.java)
            intent.putExtra(RoomChatActivity.USER_RESPONSE_FRIEND, userResponseFriend)
            intent.putExtra(RoomChatActivity.USER_RESPONSE, userResponseUser)
            startActivity(intent)
        }
    }
}