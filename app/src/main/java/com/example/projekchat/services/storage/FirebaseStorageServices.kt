package com.example.projekchat.services.storage

import android.net.Uri
import android.util.Log
import com.example.projekchat.services.firestore.FirestoreService
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class FirebaseStorageServices {
    companion object{
        const val COL_USER_DATA = "USER_DATA"
    }
    private val storageServices = FirebaseStorage.getInstance()

    suspend fun saveImage(image:Uri?, email:String) {
        try {
            if (image!=null){
                storageServices.getReference().child("${COL_USER_DATA}/${email}.png").putFile(image).await()
                var downloadURL = storageServices.getReference().child("$COL_USER_DATA/${email}.png").downloadUrl.await()
                val firestoreService = FirestoreService()
                firestoreService.updateProfileImage(downloadURL.toString(), email)
            }
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