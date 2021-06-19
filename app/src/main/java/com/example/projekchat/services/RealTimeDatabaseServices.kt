package com.example.projekchat.services

import android.util.Log
import com.example.projekchat.response.ItemMessageResponse
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RealTimeDatabaseServices {
    companion object{
        const val COL_MESSAGES = "MESSAGES"
        const val COL_CHAT_ROOM = "CHAT_ROOM"
    }
    val db = FirebaseDatabase.getInstance("https://fir-kotlin-37cdf-default-rtdb.firebaseio.com/")

    suspend fun saveMessage(item:ItemMessageResponse){
        var map = HashMap<String, Any>()
        map.put("messages", item.message!!)
        map.put("time", System.currentTimeMillis())
        map.put("from", item.sendBy!!)
        map.put("to", item.sendTo!!)
        try {
            db.getReference(COL_MESSAGES).child(COL_CHAT_ROOM).child("${item.sendBy}").child("${item.sendTo}")
                .push().setValue(map).await()
            db.getReference(COL_MESSAGES).child(COL_CHAT_ROOM).child("${item.sendTo}").child("${item.sendBy}")
                .push().setValue(map).await()
        }catch (e:Exception){
            Log.d("ERROR", e.message.toString())
        }
    }
}