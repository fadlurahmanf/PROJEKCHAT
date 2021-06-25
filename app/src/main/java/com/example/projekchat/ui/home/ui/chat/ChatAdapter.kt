package com.example.projekchat.ui.home.ui.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.response.UserResponse
import com.example.projekchat.services.auth.AuthenticationService
import com.example.projekchat.utils.OnItemClickCallback
import com.example.projekchat.utils.TimeConvert

class ChatAdapter():RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {
    private var listMessage = ArrayList<ItemMessageResponse>()
    private lateinit var userResponseUser:UserResponse
    private lateinit var userResponseFriend:UserResponse
    private var mapUser = HashMap<String, UserResponse>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    fun setMapLastMessage(map:HashMap<String, UserResponse>){
        if (map!=null){
            mapUser = map
        }
    }

    fun clear(){
        if (listMessage!=null){
            listMessage.clear()
        }
    }

    fun setListMessage(list:List<ItemMessageResponse>){
        if (list!=null){
            listMessage.clear()
            listMessage.addAll(list)
        }
    }
    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var message:TextView = itemView.findViewById(R.id.itemlastmessage_message)
        var fullname:TextView = itemView.findViewById(R.id.itemlastmessage_fullname)
        var image:ImageView = itemView.findViewById(R.id.itemlastmessage_image)
        var totalUnread:TextView = itemView.findViewById(R.id.itemlastmessage_totalUnread)
        var date:TextView = itemView.findViewById(R.id.itemlastmessage_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_last_message, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var message = listMessage[position]

        val date = TimeConvert.timestampToDay(message.time!!)
        val now = TimeConvert.timestampToDay(System.currentTimeMillis())

        var isSendByMe = sendByMe(message)
        holder.date.text = date

        holder.message.text = message.message
        holder.totalUnread.text = "NEW"

        if (isSendByMe){
            holder.fullname.text = message.sendToName
            if (message.photoSendTo!="null"){
                Glide.with(holder.image).load(message.photoSendTo).into(holder.image)
            }

        }else{
            holder.fullname.text = message.sendByName

            if(message.photoSendBy!="null"){
                Glide.with(holder.image).load(message.photoSendBy).into(holder.image)
            }
        }

        holder.itemView.setOnClickListener {
            if (isSendByMe){
                userResponseUser = getUserResponseBy(message)
                userResponseFriend = getUserResponseTo(message)
                onItemClickCallback.onItemLastMessageClicked(userResponseUser, userResponseFriend)
            }else{
                userResponseUser = getUserResponseTo(message)
                userResponseFriend = getUserResponseBy(message)
                onItemClickCallback.onItemLastMessageClicked(userResponseUser, userResponseFriend)
            }
        }

        if (isSendByMe){
            // KALAU PENGIRIM ADALAH KITA
            holder.totalUnread.visibility = View.INVISIBLE
        }else{
            if (message.totalUnread?.toInt()==0){
                //KALAU TOTAL UNREAD = 0
                holder.totalUnread.visibility = View.INVISIBLE
            }else{
                //KALAU TOTAL UNREAD >0
                holder.fullname.setTextColor(Color.parseColor("#296870"))
            }
        }

    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    private fun getEmailUser(): String? {
        val authenticationService = AuthenticationService()
        return authenticationService.isUserSignIn()?.email
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

    private fun getUserResponseBy(message: ItemMessageResponse): UserResponse {
        var userResponse = UserResponse(
                message.sendByName,
                message.sendBy,
                "",
                "",
                message.photoSendBy,
                message.tokenSendBy
        )
        return userResponse
    }
    private fun getUserResponseTo(message: ItemMessageResponse):UserResponse{
        var userResponse = UserResponse(
                message.sendToName,
                message.sendTo,
                "",
                "",
                message.photoSendTo,
                message.tokenSendTo
        )
        return userResponse
    }
}