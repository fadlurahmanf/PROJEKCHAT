package com.example.projekchat.response

data class SearchResponse(
    var friendFullname:String?="",
    var friendEmail:String?="",
    var friendProfile:String?="",
    var friendStatus:String?="",
    var status:Int?=0,
    var friendToken:String?=""
)
