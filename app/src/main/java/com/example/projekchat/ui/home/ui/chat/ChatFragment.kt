package com.example.projekchat.ui.home.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService


class ChatFragment : Fragment() {

    private lateinit var btn_coba:Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel:ChatViewModel

    private var listItemLastMessage = ArrayList<ItemMessageResponse>()

    private var adapter = ChatAdapter()

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

        listenerLastMessage()

        btn_coba.setOnClickListener {
            listenerLastMessage()
        }
    }

    private fun listenerLastMessage(){
        val fsService = FirestoreService()
        val fsmessage = FirestoreService().MessageService()
        fsmessage.getAllLastMessage(getEmailUser()!!)?.addSnapshotListener { value, error ->
            value?.documents?.forEach {
                latestMessage["${it.id}"] = ItemMessageResponse(
                        it.get("message").toString(),
                        it.get("sendBy").toString(),
                        it.get("sendTo").toString(),
                        it.get("time").toString().toLong(),
                        it.id,
                        it.get("sendByName").toString()
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
        listItemLastMessage.sortWith(compareByDescending<ItemMessageResponse>{
            it.message
        })
        adapter.setListMessage(listItemLastMessage)
        recyclerView.adapter = adapter
    }
    var latestMessage = HashMap<String, ItemMessageResponse>()

    private fun initialize(view: View) {
        recyclerView = view.findViewById(R.id.chatFragment_recylceview)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ChatViewModel::class.java]
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }

}