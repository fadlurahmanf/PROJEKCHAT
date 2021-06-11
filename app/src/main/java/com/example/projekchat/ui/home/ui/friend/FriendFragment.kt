package com.example.projekchat.ui.home.ui.friend

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.MessageResponse
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.ui.home.ui.chatlog.RoomChatActivity
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter


class FriendFragment : Fragment() {

    private lateinit var emptyText:TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading:ProgressBar

    private var dummylistUserResponse=ArrayList<UserResponse>()

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
        initializationID(view)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = FriendAdapter(dummylistUserResponse)

        GlobalScope.launch {
            withContext(Main){
                if (getListFriendOfUser().isEmpty()){
                    loading.visibility = View.INVISIBLE
                    emptyText.visibility = View.VISIBLE
                }else{
                    loading.visibility = View.INVISIBLE
                    initializeAdapter()
                }

            }
        }

    }

    private suspend fun initializeAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        observe()
    }

    private suspend fun observe() {
        var adapter = FriendAdapter(getDetailOfListFriendOfUser())
        recyclerView.adapter = adapter
        adapter.setOnItemClickCallback(object :FriendAdapter.OnItemClickCallback{
            override fun onClicked(data: UserResponse) {
                val firestoreService = FirestoreService().MessageService()
                GlobalScope.launch {
                    var chatRoomName:String = firestoreService.getNameChatRoom(getEmailUser()!!, data.email!!)
                    var messageResponse = MessageResponse(
                        emailUser = getEmailUser()!!,
                        emailFriend = data.email!!,
                        chatRoomName = chatRoomName!!
                    )
                    val intent = Intent(this@FriendFragment.context, RoomChatActivity::class.java)
                    intent.putExtra(RoomChatActivity.MESSAGE_RESPONSE, messageResponse)
                    startActivity(intent)
                }
            }

        })
    }

    private suspend fun getDetailOfListFriendOfUser(): ArrayList<UserResponse> {
        var firestoreService = FirestoreService()
        var list = ArrayList<UserResponse>()
        firestoreService.getAllUser()?.forEach {
            if (it.id.toString()!="${getEmailUser().toString()}"){
                if (getListFriendOfUser().contains(it.id.toString())){
                    list.add(
                        UserResponse(
                            fullName = it.get("FULL_NAME").toString(),
                            imageProfile = it.get("IMAGE_PROFILE").toString(),
                            status = it.get("STATUS").toString(),
                            email = it.get("EMAIL").toString()
                        )
                    )
                }
            }
        }
        return list
    }

    private suspend fun getListFriendOfUser(): ArrayList<String> {
        var firestoreService = FirestoreService()
        var list = ArrayList<String>()
        firestoreService.getListFriedUser("${getEmailUser()}")!!.documents.forEach {
            list.add(it.id.toString())
        }
        return list
    }

    private fun getEmailUser(): String? {
        var authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

    private fun initializationID(view: View) {
        recyclerView = view.findViewById(R.id.friendFragment_recycleview)
        emptyText = view.findViewById(R.id.friendFragment_empty)
        loading = view.findViewById(R.id.friendFragment_loading)
    }
}