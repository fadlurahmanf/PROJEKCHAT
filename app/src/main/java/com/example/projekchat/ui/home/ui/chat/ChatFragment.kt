package com.example.projekchat.ui.home.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.ui.home.ui.chatlog.RoomChatActivity
import com.example.projekchat.utils.OnItemClickCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatFragment : Fragment(), OnItemClickCallback {

    private lateinit var btn_coba:Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel:ChatViewModel

    private var listItemLastMessage = ArrayList<ItemMessageResponse>()
    private var mapUserResponse = HashMap<String, UserResponse>()

    private var adapter = ChatAdapter()
    var latestMessage = HashMap<String, ItemMessageResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_coba = view.findViewById(R.id.coba)
        initialize(view)

        recyclerView.layoutManager = LinearLayoutManager(this.context)

//        GlobalScope.launch {
//            withContext(Dispatchers.Main){
//                viewModel.listProfileData.observe(viewLifecycleOwner, Observer {
//                    adapter.setMapLastMessage(it)
//                    refreshRecycleView()
//                })
//            }
//        }

        listenerLastMessage()

        btn_coba.setOnClickListener {
        }
        btn_coba.visibility = View.INVISIBLE
    }

    private fun listenerLastMessage(){
        val fsmessage = FirestoreService().MessageService()
        fsmessage.getAllLastMessage(getEmailUser()!!)?.addSnapshotListener { value, error ->
            value?.documents?.forEach {
                latestMessage["${it.id}"] = ItemMessageResponse(
                        it.get("message").toString(),
                        it.get("sendBy").toString(),
                        it.get("sendTo").toString(),
                        it.get("time").toString().toLong(),
                        it.id,
                        it.get("sendByName").toString(),
                        it.get("sendToName").toString(),
                        it.get("totalUnread").toString(),
                        it.get("photoSendBy").toString(),
                        it.get("photoSendTo").toString(),
                        it.get("tokenSendBy").toString(),
                        it.get("tokenSendTo").toString()
                )
            }
            refreshRecycleView()
        }
    }

    private fun refreshRecycleView(){
        listItemLastMessage.clear()
        adapter.clear()
        latestMessage.values.forEach {
            listItemLastMessage.add(it)
        }
        //SORTING BASED ON TIME
        listItemLastMessage.sortWith(compareByDescending<ItemMessageResponse>{
            it.time
        })
        adapter.setListMessage(listItemLastMessage)
        adapter.setOnItemClickCallback(this)
        recyclerView.adapter = adapter
    }

    private fun initialize(view: View) {
        recyclerView = view.findViewById(R.id.chatFragment_recylceview)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ChatViewModel::class.java]
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

    override fun onItemLastMessageClicked(userResponse: UserResponse, userFriendResponse: UserResponse) {
        if (userResponse !=null && userFriendResponse!=null){
            val intent = Intent(this.context, RoomChatActivity::class.java)
            intent.putExtra(RoomChatActivity.USER_RESPONSE_FRIEND, userFriendResponse)
            intent.putExtra(RoomChatActivity.USER_RESPONSE, userResponse)
            startActivity(intent)
        }
    }

    override fun onItemFriendClicked(user: UserResponse) {
//        TODO("Not yet implemented")
    }


}