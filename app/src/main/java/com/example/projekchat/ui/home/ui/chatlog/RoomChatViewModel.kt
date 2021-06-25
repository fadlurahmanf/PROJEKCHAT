package com.example.projekchat.ui.home.ui.chatlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.services.firestore.FirestoreService
import com.example.projekchat.services.storage.FirebaseStorageServices
import kotlinx.coroutines.launch

class RoomChatViewModel:ViewModel() {
    private var _listProfileData = MutableLiveData<HashMap<String, UserResponse>>()
    var listProfileData:LiveData<HashMap<String, UserResponse>> = _listProfileData

    init {
        viewModelScope.launch {
            getAllProfileData()
        }
    }

    suspend fun getAllProfileData(){
        val firestoreService = FirestoreService()
        val storageServices = FirebaseStorageServices()
        var map = HashMap<String, UserResponse>()
        firestoreService.getAllUser()?.forEach {
//            var imageURL = storageServices.getImageURL("${it.get("PROFILE_IMAGE")}")
            var item = UserResponse(
                    it.get("FULL_NAME").toString(),
                    it.id,
//                    imageProfile = imageURL.toString()
            )
            map["${it.id}"] = item
        }
        _listProfileData.postValue(map)
    }

    fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }
}