package com.example.projekchat.ui.home.ui.invitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import kotlinx.coroutines.launch

class InvitationViewModel:ViewModel() {

    private var _mapUser = MutableLiveData<HashMap<String, UserResponse>>()
    var mapUser:LiveData<HashMap<String, UserResponse>> = _mapUser

    private var _listInvitation = MutableLiveData<ArrayList<UserResponse>>()
    var listInvitation:LiveData<ArrayList<UserResponse>> = _listInvitation

    init {
        viewModelScope.launch {
            getCurrentProfileData()
            getListInvitation()
        }
    }

    suspend fun getListInvitation(){
        val firestoreService = FirestoreService()
        var list = ArrayList<UserResponse>()
        firestoreService.getAllInvitation(getEmailUser())?.documents?.forEach {
            list.add(UserResponse(
                    it.get("FULL_NAME").toString(),
                    it.get("EMAIL").toString(),
                    "",
                    it.get("STATUS").toString(),
                    it.get("PROFILE_IMAGE").toString(),
                    it.get("TOKEN").toString(),
            ))
        }
        _listInvitation.postValue(list)
    }

    private fun getEmailUser(): String {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email!!
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

}