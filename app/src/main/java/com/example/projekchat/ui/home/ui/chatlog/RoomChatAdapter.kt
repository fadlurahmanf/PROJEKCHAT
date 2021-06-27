package com.example.projekchat.ui.home.ui.chatlog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.google.android.material.imageview.ShapeableImageView

class RoomChatAdapter():RecyclerView.Adapter<RoomChatAdapter.ListViewHolder>() {
    private var listMessage:ArrayList<ItemMessageResponse> = ArrayList<ItemMessageResponse>()
    private lateinit var userResponseUser: UserResponse
    private lateinit var userResponseFriend:UserResponse

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    fun setListMessage(list: List<ItemMessageResponse>){
        if (list!=null){
            this.listMessage.clear()
            this.listMessage.addAll(list)
        }
    }

    fun setUserResponseUser(user:UserResponse){
        if (user!=null){
            userResponseUser = user
        }
    }

    fun setUserResponseFriend(user:UserResponse){
        if (user!=null){
            userResponseFriend = user
        }
    }

    inner class ListViewHolder(itemView:View, viewType: Int):RecyclerView.ViewHolder(itemView) {
        var messageText:TextView = itemView.findViewById(R.id.itemchat_message)
        var image:ShapeableImageView = itemView.findViewById(R.id.itemchat_imageProfile)
        init {
            if (userResponseUser!=null&&userResponseFriend!=null){
                if (viewType==MESSAGE_RIGHT){
                    if (userResponseUser.imageProfile!="null"){
                        Glide.with(image).load(userResponseUser.imageProfile).into(image)
                    }
                }else{
                    if (userResponseFriend.imageProfile!="null"){
                        Glide.with(image).load(userResponseFriend.imageProfile).into(image)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        var view:View?=null
        if (viewType==MESSAGE_LEFT){
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right, parent, false)
        }
        return ListViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val message = listMessage[position]
        holder.messageText.text = "${message.message}"

    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        if (listMessage[position].sendBy==getEmailUser()){
            return MESSAGE_RIGHT
        }else{
            return MESSAGE_LEFT
        }
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }
}