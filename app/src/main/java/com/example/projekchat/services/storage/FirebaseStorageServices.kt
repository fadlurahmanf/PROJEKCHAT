package com.example.projekchat.services.storage

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class FirebaseStorageServices {
    companion object{
        const val COL_USER_DATA = "USER_DATA"
    }
    private val storageServices = FirebaseStorage.getInstance()

    fun saveImage(image:Uri, email:String){
        try {
            storageServices.getReference().child("${COL_USER_DATA}/${email}.png").putFile(image)
        }catch (e:Exception){
            Log.d("FIREBASE STORAGE", "${e.message}")
        }
    }

    suspend fun getImageURL(imageName:String): Uri? {
        try {
            return storageServices.getReference().child("${COL_USER_DATA}/$imageName").downloadUrl.await()
        }catch (e:Exception){
            Log.d("FIREBASE STORAGE", "${e.message}")
            return null
        }
    }
}