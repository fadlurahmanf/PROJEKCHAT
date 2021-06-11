package com.example.projekchat.ui.home.ui.chatlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.MessageResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RoomChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var input_message:EditText
    private lateinit var btn_send:Button

    private lateinit var btn:Button

    lateinit var messageResponse: MessageResponse
    var listConversation:ArrayList<ItemMessageResponse> = ArrayList<ItemMessageResponse>()

    companion object{
        const val MESSAGE_RESPONSE = "MESSAGE_RESPONSE"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_chat)
        initializeID()
        initializeData()

        GlobalScope.launch {
            getAllConversation()
            withContext(Dispatchers.Main){
                initializeAdapter()
            }
        }

        btn_send.setOnClickListener {
            GlobalScope.launch {
                var token = getInformationReceiverMessage()
                getToken("${input_message.text.toString()}", "${token}")
                var firestoreService = FirestoreService()
                firestoreService.MessageService().sendMessage(messageResponse.chatRoomName, input_message.text.toString(),
                messageResponse.emailFriend, messageResponse.emailUser)
                withContext(Dispatchers.Main){
                    input_message.text.clear()
                }
            }
        }

    }

    private suspend fun getInformationReceiverMessage(): String {
        val firestoreService = FirestoreService()
        var result = firestoreService.getProfileData(messageResponse.emailFriend)
        return result?.get("TOKEN").toString()
    }

    private fun initializeAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RoomChatAdapter(listConversation, getEmailUser()!!)
    }

    private suspend fun getAllConversation(){
        var firestoreService = FirestoreService().MessageService()
        firestoreService.getAllConversation(messageResponse.chatRoomName)?.forEach {
            listConversation.add(
                ItemMessageResponse(
                    "${it.get("MESSAGE")}",
                    "${it.get("SENT_BY")}",
                    it.getLong("TIME")!!
                )
            )
        }
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()!!.email
    }

    private fun initializeData() {
        var extras = intent.extras
        messageResponse = extras?.getParcelable<MessageResponse>(MESSAGE_RESPONSE) as MessageResponse
    }

    private fun initializeID() {
        recyclerView = findViewById(R.id.chatroomActivity_recycleview)
        input_message = findViewById(R.id.chatroomActivity_inputmessage)
        btn_send = findViewById(R.id.chatroomActivity_btn_send)
    }

    private fun getToken(message:String, token:String){
//        val token = "eYQbq-TISsOwuEaorbqjm8:APA91bFxp19fGpfpgZjhjAt7JjVbOHmaLq5KkxAI77XMi40rO0a5JVvsb33Gqbhp541wCu9WGcXRx_OeVxn0-7aPPgQPIQ8k0efw50ktiIZw651QqMGUnsU5gdwnMtne_eFrk3akQef1"

        val to = JSONObject()
        val data = JSONObject()

        data.put("hisId", "ID")
//        data.put("hisImage", )
        data.put("title", "NAMA")
        data.put("message", "${message.toString()}")
        data.put("chatId", "ID CHAT")

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

                    Log.d("TAG", "onResponse: $response")
                },
                Response.ErrorListener {

                    Log.d("TAG", "onError: $it")
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