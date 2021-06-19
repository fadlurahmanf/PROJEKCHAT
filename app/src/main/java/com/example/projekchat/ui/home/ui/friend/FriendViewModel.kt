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
    private var _listFriend = MutableLiveData<List<UserResponse>>()
    var listfriend : LiveData<List<UserResponse>> = _listFriend

    private var _listFriendName = MutableLiveData<List<String>>()


    init {
        viewModelScope.launch {
            getOurData(getCurrentUser()!!)
            getAllFriendNamed()
            getAllFriendData()
        }
    }

    private fun getCurrentUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email

    }

    private  suspend fun getOurData(email:String) {
        val firestoreService = FirestoreService()
        val result = firestoreService.getProfileData(email)
        var userResponse = UserResponse(
                result?.get("FULL_NAME").toString(),
                result?.get("EMAIL").toString(),
                result?.get("PASSWORD").toString(),
                result?.get("STATUS").toString(),
                result?.get("IMAGE_PROFILE").toString(),
        )
        _userResponseUser.postValue(userResponse)
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
        val list = ArrayList<UserResponse>()
        _listFriendName.value?.forEach {
            var query = firestoreService.getProfileData(it)
            var userResponse = UserResponse(
                    query?.get("FULL_NAME")?.toString(), query?.get("EMAIL")?.toString(), status = query?.get("STATUS")?.toString(), imageProfile = query?.get("IMAGE_PROFILE")?.toString(), token = query?.get("TOKEN").toString()
            )
            list.add(userResponse)
        }
        _listFriend.value = list
    }

    fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }
}