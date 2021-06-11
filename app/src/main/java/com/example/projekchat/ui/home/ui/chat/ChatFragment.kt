package com.example.projekchat.ui.home.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.projekchat.R
import com.example.projekchat.services.message.FirebaseMessagingService
import com.example.projekchat.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class ChatFragment : Fragment() {

    private lateinit var btntoken:Button
    private lateinit var mesage:EditText

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
        btntoken = view.findViewById(R.id.gettoken)
        mesage = view.findViewById(R.id.message)

        btntoken.setOnClickListener {
            getToken(mesage.text.toString())
        }
    }

    private fun getToken(message:String){
        val token = "eYQbq-TISsOwuEaorbqjm8:APA91bFxp19fGpfpgZjhjAt7JjVbOHmaLq5KkxAI77XMi40rO0a5JVvsb33Gqbhp541wCu9WGcXRx_OeVxn0-7aPPgQPIQ8k0efw50ktiIZw651QqMGUnsU5gdwnMtne_eFrk3akQef1"

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

    private fun sendNotification(to:JSONObject){
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

        val requestQueue = Volley.newRequestQueue(this.context)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }

}