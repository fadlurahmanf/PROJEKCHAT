package com.example.projekchat.services.message

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

open class FirebaseMessagingService {
    companion object{
        const val SUCCESS = "SUCCESS"
        const val FAIL = "FAIL"
    }
    suspend fun getToken(): String? {
        try {
            return FirebaseMessaging.getInstance().token.await()
        }catch (e:Exception){
            return FAIL
        }
    }
}