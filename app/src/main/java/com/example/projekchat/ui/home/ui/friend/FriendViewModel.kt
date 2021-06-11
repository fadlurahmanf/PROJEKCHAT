package com.example.projekchat.ui.home.ui.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import kotlinx.coroutines.launch

class FriendViewModel:ViewModel() {
    private var _listFriend = MutableLiveData<List<UserResponse>>()
    var listfriend : LiveData<List<UserResponse>> = _listFriend

    private var _listFriendName = MutableLiveData<List<String>>()



    init {
        viewModelScope.launch {
            getAllFriendNamed()
        }
    }

    suspend fun getAllFriendNamed(){
        val firestoreService = FirestoreService()
        val list = ArrayList<String>()
        firestoreService.getListFriedUser(getEmailUser()!!)?.forEach {
            list.add(it.id)
        }
        _listFriendName.value = list
    }

    suspend fun getAllFriendData(){
        val firestoreService = FirestoreService()
        _listFriendName.value?.forEach {

        }
    }

    fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }
}