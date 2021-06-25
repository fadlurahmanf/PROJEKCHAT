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
    private var mapUser = HashMap<String, UserResponse>()
    private lateinit var emailuser:String

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    fun setListMessage(list: List<ItemMessageResponse>){
        if (list!=null){
            this.listMessage.clear()
            this.listMessage.addAll(list)
        }
    }


    fun setEmailUser(email:String){
        if (email!=null){
            this.emailuser = email
        }
    }

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var messageText:TextView = itemView.findViewById(R.id.itemchat_message)
        var image:ShapeableImageView = itemView.findViewById(R.id.itemchat_imageProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        var view:View?=null
        if (viewType==MESSAGE_LEFT){
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_left, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_right, parent, false)
        }
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val message = listMessage[position]
        holder.messageText.text = "${message.message}"

        var isSendByMe = sendByMe(message)

        if (isSendByMe){
            if (message.photoSendBy!="null"){
                Glide.with(holder.image).load(message.photoSendBy).into(holder.image)
            }
        }else{
            if(message.photoSendTo!="null"){
                Glide.with(holder.image).load(message.photoSendTo).into(holder.image)
            }
        }

    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        if (listMessage[position].sendBy==emailuser){
            return MESSAGE_RIGHT
        }else{
            return MESSAGE_LEFT
        }
    }

    private fun sendByMe(message: ItemMessageResponse): Boolean {
        var isSendByMe:Boolean = true
        if (message.sendBy==getEmailUser()){
            isSendByMe = true
        }else{
            isSendByMe = false
        }
        return isSendByMe
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
    }
}