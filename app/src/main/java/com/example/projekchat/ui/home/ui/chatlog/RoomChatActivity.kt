package com.example.projekchat.ui.home.ui.chatlog

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.utils.Constant
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RoomChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var input_message:EditText
    private lateinit var btn_send:Button

    private lateinit var viewModel: RoomChatViewModel

    private lateinit var userResponseFriend: UserResponse
    private lateinit var userResponseUser:UserResponse
    private lateinit var chatRoomName:String

    private var listMessage = ArrayList<ItemMessageResponse>()

    private var adapter = RoomChatAdapter()

    companion object{
        const val USER_RESPONSE_FRIEND = "USER_RESPONSE_FRIEND"
        const val USER_RESPONSE = "USER_RESPONSE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_chat)
        initialize()
        initializeData()

        updateTotalUnread(userResponseUser.email!!, userResponseFriend.email!!)
        listenChatConversation(userResponseUser.email!!, userResponseFriend.email!!)

        btn_send.setOnClickListener {
            var message = input_message.text.toString()
            input_message.text.clear()
            getToken(message, userResponseFriend.token!!, userResponseUser.fullName!!)
            println(userResponseFriend.toString())
            println(userResponseUser.toString())
            GlobalScope.launch {
                if (userResponseUser!=null&&userResponseFriend!=null){
                    val item = ItemMessageResponse(
                            message,
                            userResponseUser.email,
                            userResponseFriend.email,
                            sendByName = userResponseUser.fullName,
                            sendToName = userResponseFriend.fullName,
                            photoSendBy = userResponseUser.imageProfile,
                            photoSendTo = userResponseFriend.imageProfile,
                            tokenSendBy = userResponseUser.token,
                            tokenSendTo = userResponseFriend.token,
                    )
                    val db = FirestoreService().MessageService()
                    db.sendMessage(item)
                    withContext(Dispatchers.Main){
                        recyclerView.scrollToPosition(listMessage.size-1)
                    }
                }
            }
        }
        adapter.setListMessage(listMessage)
        recyclerView.adapter = adapter
    }

    private fun updateTotalUnread(emailUser: String, emailFriend: String) {
        val fsmessage = FirestoreService().MessageService()
        fsmessage.updateTotalUnread(emailUser, emailFriend)

    }

    private fun listenChatConversation(emailUser:String, emailFriend:String){
        var ms = FirestoreService().MessageService()
        ms.getConversation(emailUser, emailFriend)?.addSnapshotListener { value, error ->
            if (error!=null){
                Snackbar.make(this, recyclerView, "${error.message}", Snackbar.LENGTH_SHORT).show()
            }
            if (listMessage!=null){
                listMessage.clear()
            }
            value?.forEach {
                val item = ItemMessageResponse(
                        it.get("message").toString(),
                        it.get("sendBy").toString(),
                        it.get("sendTo").toString(),
                        it.getLong("time"),
                        it.id,
                        it.get("sendByName").toString(),
                        it.get("sendToName").toString(),
                        "",
                        it.get("photoSendBy").toString(),
                        it.get("photoSendTo").toString(),
                        it.get("tokenSendBy").toString(),
                        it.get("tokenSendTo").toString()

                )
                listMessage.add(item)
            }
            adapter.setListMessage(listMessage)
            recyclerView.adapter = adapter
            recyclerView.scrollToPosition(listMessage.size-1)
        }
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()!!.email
    }

    private fun initializeData() {
        userResponseFriend = intent?.getParcelableExtra<UserResponse>(USER_RESPONSE_FRIEND) as UserResponse
        userResponseUser = intent?.getParcelableExtra<UserResponse>(USER_RESPONSE) as UserResponse
        supportActionBar?.title = userResponseFriend.fullName

        adapter.setUserResponseUser(userResponseUser)
        adapter.setUserResponseFriend(userResponseFriend)
    }

    private fun initialize() {
        recyclerView = findViewById(R.id.chatroomActivity_recycleview)
        input_message = findViewById(R.id.chatroomActivity_inputmessage)
        btn_send = findViewById(R.id.chatroomActivity_btn_send)

        recyclerView.layoutManager = LinearLayoutManager(this@RoomChatActivity)
        recyclerView.setHasFixedSize(true)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[RoomChatViewModel::class.java]
    }

    private fun getToken(message: String, token: String, fullName: String){
        val to = JSONObject()
        val data = JSONObject()
        val tokenBaru = "eYQbq-TISsOwuEaorbqjm8:APA91bFxp19fGpfpgZjhjAt7JjVbOHmaLq5KkxAI77XMi40rO0a5JVvsb33Gqbhp541wCu9WGcXRx_OeVxn0-7aPPgQPIQ8k0efw50ktiIZw651QqMGUnsU5gdwnMtne_eFrk3akQef1"

        data.put("hisId", "ID")
//        data.put("hisImage", )
        data.put("title", fullName)
        data.put("message", "${message.toString()}")
        data.put("chatId", "ID CHAT")

        data.put("userEmail", userResponseUser.email)
        data.put("userName", userResponseUser.fullName)
        data.put("userToken", userResponseUser.token)
        data.put("userImage", userResponseUser.imageProfile)

        data.put("friendEmail", userResponseFriend.email)
        data.put("friendName", userResponseFriend.fullName)
        data.put("friendToken", userResponseFriend.token)
        data.put("friendImage", userResponseFriend.imageProfile)

        to.put("to", token)
        to.put("data", data)
        sendNotification(to)
    }

    private fun sendNotification(to: JSONObject){
        val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                Constant.NOTIFICATION_URL,
                to,
                Response.Listener { response: JSONObject ->
                    Log.d("SEND_NOTIFICATION", "onResponse: $response")
                },
                Response.ErrorListener {
                    Log.d("SEND_NOTIFICATION", "onError: ${it.message}")
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + Constant.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }
}