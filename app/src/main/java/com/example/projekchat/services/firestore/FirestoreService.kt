package com.example.projekchat.services.firestore

import com.example.projekchat.response.UserResponse
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirestoreService {
    companion object{
        const val SUCCESS = "SUCCESS"
        const val FAIL = "FAIL"
        const val COL_USERDATA = "USERDATA"
        const val COL_INVITATION = "INVITATION"
        const val COL_FRIENDS = "FRIENDS"
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

    suspend fun getListFriedUser(emailUser: String): QuerySnapshot? {
        try {
            return db.collection(COL_USERDATA).document(emailUser).collection(COL_FRIENDS).get().await()
        }catch (e:Exception){
            return null
        }
    }
}