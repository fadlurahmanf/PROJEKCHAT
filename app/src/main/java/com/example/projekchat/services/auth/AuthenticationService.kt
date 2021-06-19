package com.example.projekchat.services.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.tasks.await



class AuthenticationService {
    companion object{
        const val SUCCESS = "SUCCESS"
        const val FAIL = "FAIL"
    }
    val authenticationService = FirebaseAuth.getInstance()

    suspend fun regisData(email:String, password:String): String {
        var message = FAIL
        try {
            authenticationService.createUserWithEmailAndPassword(email, password).await()
            message = SUCCESS
            return message
        }catch (e:Exception){
            message = e.message.toString()
            return message
        }
    }

    suspend fun loginUser(email: String, password: String): String {
        var message = FAIL
        try {
            authenticationService.signInWithEmailAndPassword(email, password).await()
            message = SUCCESS
            return message
        }catch (e:Exception){
            message = e.message.toString()
            return message
        }
    }

    fun isUserSignIn(): FirebaseUser? {
        try {
            var result = authenticationService.currentUser
            return result
        }catch (e:Exception){
            return null
        }
    }

    fun logOutUser(){
        authenticationService.signOut()
    }
}