package com.example.projekchat.utils

import com.example.projekchat.response.UserResponse

interface OnItemClickCallback {
    fun onItemLastMessageClicked(userResponse: UserResponse, userFriendResponse: UserResponse)
    fun onItemFriendClicked(user:UserResponse)
}