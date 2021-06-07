package com.example.projekchat.response

import android.os.Parcelable

data class UserResponse(
    var fullName:String?="",
    var email:String?="",
    var password:String?="",
    var status:String?="",
    var imageProfile:String?=""
)
