package com.example.projekchat.services.firestore

import android.util.Log
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.UserResponse
import com.google.firebase.firestore.*
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
        const val COL_MESSAGE_TO = "TO"

        const val COL_LATEST_MESSAGES = "LATEST_MESSAGES"
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

    suspend fun updateProfileImage(photoName:String, email:String){
        var content = HashMap<String, Any>()
        content.put("PROFILE_IMAGE", photoName)
        db.collection(COL_USERDATA).document(email).update("PROFILE_IMAGE", photoName).await()
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

    suspend fun sendingInvitation(invitationBy:UserResponse, invitationTo:UserResponse): String {
        var message = FAIL
        var mapUser = HashMap<String, Any>()
        mapUser.put("EMAIL", invitationBy.email!!)
        mapUser.put("FULL_NAME", invitationBy.fullName!!)
        mapUser.put("PROFILE_IMAGE", invitationBy.imageProfile!!)
        mapUser.put("TOKEN", invitationBy.token!!)
        mapUser.put("STATUS", invitationBy.status!!)
        try {
            db.collection(COL_USERDATA).document(invitationTo.email!!).collection(COL_INVITATION)
                .document(invitationBy.email!!).set(mapUser).await()
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

    suspend fun setFriend(userResponseUser:UserResponse, userResponseFriend:UserResponse): String {
        var message = FAIL
        var mapEmailUser = HashMap<String, Any>()
        mapEmailUser.put("EMAIL", userResponseUser.email!!)
        mapEmailUser.put("FULL_NAME", userResponseUser.fullName!!)
        mapEmailUser.put("TOKEN", userResponseUser.token!!)
        mapEmailUser.put("STATUS", userResponseUser.status!!)
        mapEmailUser.put("PROFILE_IMAGE", userResponseUser.imageProfile!!)
        var mapEmailFriend = HashMap<String, Any>()
        mapEmailFriend.put("EMAIL", userResponseFriend.email!!)
        mapEmailFriend.put("FULL_NAME", userResponseFriend.fullName!!)
        mapEmailFriend.put("TOKEN", userResponseFriend.token!!)
        mapEmailFriend.put("STATUS", userResponseFriend.status!!)
        mapEmailFriend.put("PROFILE_IMAGE", userResponseFriend.imageProfile!!)
        try {
            db.collection(COL_USERDATA).document(userResponseUser.email!!).collection(COL_FRIENDS).document(userResponseFriend.email!!).set(mapEmailFriend).await()
            db.collection(COL_USERDATA).document(userResponseFriend.email!!).collection(COL_FRIENDS).document(userResponseUser.email!!).set(mapEmailUser).await()
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

        fun getAllLastMessage(email: String): Query? {
            try {
                return db.collection(COL_LATEST_MESSAGES).document(email).collection(COL_MESSAGE_TO).orderBy("time", Query.Direction.DESCENDING)
            }catch (e:Exception){
                Log.d("FIRESTORE SERVICE", "${e.message}")
                return null
            }
        }

        fun getListMessageTo(fromEmail:String): CollectionReference? {
            try {
                return db.collection(COL_CHAT_ROOM).document(fromEmail).collection(COL_MESSAGE_TO)
            }catch (e:Exception){
                return null
            }
        }

        fun updateTotalUnread(emailUser: String, emailFriend: String){
            db.collection(COL_LATEST_MESSAGES).document(emailUser).collection(COL_MESSAGE_TO).document(emailFriend)
                    .update("totalUnread", 0)
        }

        fun getConversation(emailUser: String, emailFriend: String): Query? {
            try {
                return db.collection(COL_CHAT_ROOM).document(emailUser).collection(COL_MESSAGE_TO).document(emailFriend)
                    .collection(COL_MESSAGES).orderBy("time", Query.Direction.ASCENDING)
            }catch (e:Exception){
                return null
            }
        }

        suspend fun createChatRoom(emailUser: String, emailFriend: String){
            var contentUser = HashMap<String, Any>()
            contentUser.put("EMAIL", emailUser)
            val contentFriend = HashMap<String, Any>()
            contentFriend.put("EMAIL", emailFriend)
            try {
                db.collection(COL_CHAT_ROOM).document(emailUser).set(contentUser).await()
                db.collection(COL_CHAT_ROOM).document(emailFriend).set(contentFriend).await()
                db.collection(COL_CHAT_ROOM).document(emailUser).collection(COL_MESSAGE_TO).document(emailFriend).set(contentFriend).await()
                db.collection(COL_CHAT_ROOM).document(emailFriend).collection(COL_MESSAGE_TO).document(emailUser).set(contentUser).await()
            }catch (e:Exception){
                Log.d("ERROR", e.message.toString())
            }
        }


        suspend fun sendMessage(item:ItemMessageResponse) {
            println("${item.photoSendBy}   ${item.photoSendTo}")
            GlobalScope.launch {
                var totalUnread:Int = 0
                var query= db.collection(COL_LATEST_MESSAGES).document("${item.sendBy}").collection(COL_MESSAGE_TO)
                        .document("${item.sendTo}").get().await()

                if (query.exists()){
                    if (query.get("totalUnread")==null){
                        totalUnread = 1
                    }else{
                        totalUnread = query.get("totalUnread").toString().toInt()
                        totalUnread = totalUnread+1
                    }
                    Log.d("FIRESTORE SERVICE", "QUERY EXIST ${totalUnread}")
                }else{
                    Log.d("FIRESTORE SERVICE", "QUERY TIDAK EXIST")
                    totalUnread = 1
                }

                var content = createContent(item, totalUnread)
                println("TOTAL UNREAD $totalUnread content ${item.toString()}")
                Log.d("FIRESTORE SERVICE", "CONTENT ${content.toString()}")
                db.collection(COL_CHAT_ROOM).document("${item.sendBy}").collection(COL_MESSAGE_TO)
                        .document("${item.sendTo}").collection(COL_MESSAGES).add(content).await()
                db.collection(COL_CHAT_ROOM).document("${item.sendTo}").collection(COL_MESSAGE_TO)
                        .document("${item.sendBy}").collection(COL_MESSAGES).add(content).await()
                db.collection(COL_LATEST_MESSAGES).document("${item.sendBy}").collection(COL_MESSAGE_TO)
                        .document("${item.sendTo}").set(content).await()
                db.collection(COL_LATEST_MESSAGES).document("${item.sendTo}").collection(COL_MESSAGE_TO)
                        .document("${item.sendBy}").set(content).await()
            }
        }

        private fun createContent(item: ItemMessageResponse, totalUnread:Int):HashMap<String, Any> {
            var content = HashMap<String, Any>()
            content.put("message", item.message!!)
            content.put("sendBy", item.sendBy!!)
            content.put("sendTo", item.sendTo!!)
            content.put("time", System.currentTimeMillis())
            content.put("sendByName", item.sendByName!!)
            content.put("sendToName", item.sendToName!!)
            content.put("photoSendBy", item.photoSendBy.toString())
            content.put("photoSendTo", item.photoSendTo.toString())
            content.put("tokenSendBy", item.tokenSendBy!!)
            content.put("tokenSendTo", item.tokenSendTo!!)
            content.put("totalUnread", totalUnread)
            return content
        }
    }
}