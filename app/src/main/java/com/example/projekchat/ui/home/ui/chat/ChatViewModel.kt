package com.example.projekchat.ui.home.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService

class ChatViewModel:ViewModel() {
    var _listMessageTo = MutableLiveData<List<String>>()
    var listMessageTo:LiveData<List<String>> = _listMessageTo

    var _listLastMessage = MutableLiveData<List<ItemMessageResponse>>()
    var listLastMessage :LiveData<List<ItemMessageResponse>> = _listLastMessage

    init {
//        getListMessageTo(getEmailUser()!!)
    }

    fun getListMessageTo(email:String){
        val fsmessage = FirestoreService().MessageService()
        val list = ArrayList<String>()
        fsmessage.getListMessageTo(email)?.addSnapshotListener { value, error ->
            println("valueeeeeee ${value?.size()}")
            println("errorrrrrr nya ${error?.message}")
            value?.documents?.forEach {
                println(it.id)
                list.add(it.id)
            }
            _listMessageTo.postValue(list)
        }
    }

    fun getLastMessage(toEmail:List<String>){
//        val fsmessage = FirestoreService().MessageService()
//        var list = ArrayList<ItemMessageResponse>()
//        toEmail.forEach {
//            fsmessage.getLastConversation(getEmailUser()!!, it)?.addSnapshotListener { value, error ->
//                value?.documents?.forEach {
//                    println(it.id)
//                    list.add(ItemMessageResponse(
//                            it.get("message").toString(),
//                            it.get("sendBy").toString(),
//                            it.get("sendTo").toString(),
//                            it.getLong("time"),
//                            it.id
//                    ))
//                }
//                _listLastMessage.postValue(list)
//            }
//        }
//        return listLastMessage
    }

    fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }
}