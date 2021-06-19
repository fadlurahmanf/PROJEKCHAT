package com.example.projekchat.utils

import com.example.projekchat.response.UserResponse

interface OnItemClickCallback {
    fun onItemFriendClicked(user:UserResponse)
}