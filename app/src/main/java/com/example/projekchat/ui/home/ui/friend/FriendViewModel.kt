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
    private var _userResponseUser = MutableLiveData<UserResponse>()
    var userResponseUser:LiveData<UserResponse> = _userResponseUser

    private var _mapAllUser = MutableLiveData<HashMap<String, UserResponse>>()
    var mapAllUser:LiveData<HashMap<String, UserResponse>> = _mapAllUser

    private var _listFriend = MutableLiveData<List<UserResponse>>()
    var listfriend : LiveData<List<UserResponse>> = _listFriend

    private var _listFriendName = MutableLiveData<List<String>>()


    init {
        viewModelScope.launch {
            getAllUserData()
            getAllFriend()
        }
    }

    private suspend fun getAllFriend(){
        val fsService = FirestoreService()
        var list = ArrayList<UserResponse>()
        fsService.getListFriedUser(getCurrentUser()!!)?.forEach {
            list.add(UserResponse(
                    it.get("FULL_NAME").toString(),
                    it.get("EMAIL").toString(),
                    "",
                    it.get("STATUS").toString(),
                    it.get("PROFILE_IMAGE").toString(),
                    it.get("TOKEN").toString()
            ))
        }
        _listFriend.postValue(list)
    }

    private suspend fun getAllUserData(){
        val fsService = FirestoreService()
        var map = HashMap<String, UserResponse>()
        fsService.getAllUser()?.forEach {
            map["${it.id}"] = UserResponse(
                    it.get("FULL_NAME").toString(),
                    it.get("EMAIL").toString(),
                    "",
                    it.get("STATUS").toString(),
                    it.get("PROFILE_IMAGE").toString(),
                    it.get("TOKEN").toString()
            )
        }
        _mapAllUser.postValue(map)
    }

    private fun getCurrentUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email

    }
}