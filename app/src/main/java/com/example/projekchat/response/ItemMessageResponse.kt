package com.example.projekchat.response

data class ItemMessageResponse(
    var message:String?="",
    var sendBy:String?="",
    var sendTo:String?="",
    var time:Long?=0,
    var messageID:String?="",
    var sendByName:String?="",
    var sendToName:String?="",
    var totalUnread:String?="",
    var photoSendBy:String?="",
    var photoSendTo:String?="",
    var tokenSendBy:String?="",
    var tokenSendTo:String?=""
)
