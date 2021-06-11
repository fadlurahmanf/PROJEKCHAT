package com.example.projekchat.services.firestore

import com.example.projekchat.response.MessageResponse
import com.example.projekchat.response.UserResponse
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreService {
    companion object{
        const val SUCCESS = "SUCCESS"
        const val FAIL = "FAIL"
        const val COL_USERDATA = "USERDATA"
        const val COL_INVITATION = "INVITATION"
        const val COL_FRIENDS = "FRIENDS"

        const val COL_CHAT_ROOM = "CHAT_ROOM"
        const val COL_MESSAGES = "MESSAGES"
    }
    val db = FirebaseFirestore.getInstance()

    suspend fun setProfileData(userData: HashMap<String, Any>): String {
        var message = FAIL
        try {
            db.collection(COL_USERDATA).document("${userData["EMAIL"]}").set(userData).await()
            message = SUCCESS
            return message
        }catch (e:Exception){
            message = e.message.toString()
            return message
        }
    }

    suspend fun getProfileData(email:String): DocumentSnapshot? {
        try {
            return db.collection(COL_USERDATA).document(email).get().await()
        }catch (e:Exception){
            return null
        }
    }

    suspend fun setUpdateProfileData(emailUser: String,content:HashMap<String, Any>): String {
        var message = FAIL
        try {
            db.collection(COL_USERDATA).document(emailUser).update(content).await()
            message = SUCCESS
        }catch (e:Exception){
            message = e.message.toString()
        }
        return message
    }

    suspend fun getAllUser(): MutableList<DocumentSnapshot>? {
        try {
            var result = db.collection(COL_USERDATA).get().await().documents
            return result
        }catch (e:Exception){
            return null
        }
    }

    suspend fun sendingInvitation(emailUser:String, emailFriend:String): String {
        var message = FAIL
        var mapUser = HashMap<String, Any>()
        mapUser.put("INVITATION_BY", emailUser)
        try {
            db.collection(COL_USERDATA).document(emailFriend).collection(COL_INVITATION)
                .document(emailUser).set(mapUser).await()
            message = SUCCESS
            return message
        }catch (e:Exception){
            message = e.message.toString()
            return message
        }
    }

    suspend fun getAllInvitation(emailUser: String): QuerySnapshot? {
        try {
            var result = db.collection(COL_USERDATA).document(emailUser).collection(COL_INVITATION).get().await()
            return result
        }catch (e:Exception){
            return null
        }
    }

    suspend fun setFriend(emailUser: String, emailFriend: String): String {
        var message = FAIL
        var mapEmailUser = HashMap<String, Any>()
        mapEmailUser.put("EMAIL", emailUser)
        var mapEmailFriend = HashMap<String, Any>()
        mapEmailFriend.put("EMAIL", emailFriend)
        try {
            db.collection(COL_USERDATA).document(emailUser).collection(COL_FRIENDS).document(emailFriend).set(mapEmailFriend).await()
            db.collection(COL_USERDATA).document(emailFriend).collection(COL_FRIENDS).document(emailUser).set(mapEmailUser).await()
            message = SUCCESS
            return message
        }catch (e:Exception){
            message = e.message.toString()
            return message
        }
    }

    suspend fun removeInvitation(emailUser: String,emailFriend: String): String {
        var message:String = FAIL
        try {
            db.collection(COL_USERDATA).document(emailUser).collection(COL_INVITATION).document(emailFriend).delete().await()
            message = SUCCESS
            return message
        }catch (e:Exception){
            message = e.message.toString()
            return message
        }
    }

    suspend fun getListFriedUser(emailUser: String): MutableList<DocumentSnapshot>? {
        try {
            return db.collection(COL_USERDATA).document(emailUser).collection(COL_FRIENDS).get().await().documents
        }catch (e:Exception){
            return null
        }
    }

    inner class MessageService(){
        suspend fun getAllConversation(chatRoomName:String): MutableList<DocumentSnapshot>? {
            try {
                return db.collection(COL_CHAT_ROOM).document(chatRoomName).collection(COL_MESSAGES)
                    .orderBy("TIME", Query.Direction.ASCENDING).get().await().documents
            }catch (e:Exception){
                return null
            }
        }

        suspend fun createRoomChat(chatRoomName: String, emailUser: String, emailFriend: String): String? {
            var message:String?= FAIL
            val content = HashMap<String,Any>()
            content.put("USER_0", emailUser)
            content.put("USER_1", emailFriend)
            try {
                db.collection(COL_CHAT_ROOM).document(chatRoomName).set(content).await()
                message = SUCCESS
            }catch (e:Exception){
                message = FAIL
            }
            return message
        }

        suspend fun getNameChatRoom(emailUser: String, emailFriend: String): String {
            var chatRoomName:String?= null
            try {
                var query = db.collection(COL_CHAT_ROOM).get().await().documents
                query.forEach {
                    if (it.id=="${emailUser}_${emailFriend}"){
                        println("MASUK PERTAMA")
                        chatRoomName="${emailUser}_${emailFriend}"
                    }else if (it.id=="${emailFriend}_${emailUser}"){
                        println("MASUK KEDUA")
                        chatRoomName="${emailFriend}_${emailUser}"
                    }
                }
                if (chatRoomName==null){
                    chatRoomName="${emailUser}_${emailFriend}"
                    GlobalScope.launch {
                        createRoomChat(chatRoomName!!, emailUser, emailFriend)
                    }
                }
            }catch (e:Exception){
                chatRoomName = null
            }
            return chatRoomName!!
        }

        suspend fun sendMessage(chatRoomName: String,message:String, emailSender: String, emailReceiver: String): String? {
            var messageResult:String?= FAIL
            val content = HashMap<String, Any>()
            content.put("MESSAGE", message)
            content.put("SENT_BY", emailSender)
            content.put("TIME", System.currentTimeMillis())
            try {
                db.collection(COL_CHAT_ROOM).document(chatRoomName).collection(COL_MESSAGES).add(content).await()
                messageResult = SUCCESS
            }catch (e:Exception){
                messageResult = e.message.toString()
            }
            return messageResult
        }
    }
}