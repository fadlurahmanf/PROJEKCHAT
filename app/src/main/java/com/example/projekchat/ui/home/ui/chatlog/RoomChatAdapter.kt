package com.example.projekchat.ui.home.ui.chatlog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse

class RoomChatAdapter():RecyclerView.Adapter<RoomChatAdapter.ListViewHolder>() {
    private var listMessage:ArrayList<ItemMessageResponse> = ArrayList<ItemMessageResponse>()
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
}