package com.example.projekchat.ui.home.ui.searchfriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import kotlinx.coroutines.launch

class SearchFriendViewModel:ViewModel() {
    private var _mapUser = MutableLiveData<HashMap<String, UserResponse>>()
    var mapUser:LiveData<HashMap<String, UserResponse>> = _mapUser

    init {
        viewModelScope.launch {
            getCurrentProfileData()
        }
    }

    private suspend fun getCurrentProfileData(): LiveData<HashMap<String, UserResponse>> {
        val fsService = FirestoreService()
        var map = HashMap<String, UserResponse>()
        var query = fsService.getProfileData(getEmailUser())
        map["${query?.id}"] = UserResponse(
                query?.get("FULL_NAME").toString(),
                query?.id,
                "",
                query?.get("STATUS").toString(),
                query?.get("PROFILE_IMAGE").toString(),
                query?.get("TOKEN").toString()
        )
        _mapUser.postValue(map)
        return mapUser
    }

    private fun getEmailUser(): String {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email!!
    }
}