package com.example.projekchat.response

data class ItemMessageResponse(
    var message:String?="",
    var sendBy:String?="",
    var sendTo:String?="",
    var time:Long?=0,
    var messageID:String?="",
    var sendByName:String?=""
)
