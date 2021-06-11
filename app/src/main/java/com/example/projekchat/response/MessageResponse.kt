package com.example.projekchat.response

import android.os.Parcel
import android.os.Parcelable

data class MessageResponse(
    var emailUser: String,
    var emailFriend:String,
    var chatRoomName:String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(emailUser)
        parcel.writeString(emailFriend)
        parcel.writeString(chatRoomName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MessageResponse> {
        override fun createFromParcel(parcel: Parcel): MessageResponse {
            return MessageResponse(parcel)
        }

        override fun newArray(size: Int): Array<MessageResponse?> {
            return arrayOfNulls(size)
        }
    }
}