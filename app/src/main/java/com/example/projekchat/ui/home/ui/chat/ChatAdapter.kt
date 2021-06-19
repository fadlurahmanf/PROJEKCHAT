package com.example.projekchat.ui.home.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.ItemMessageResponse
import com.example.projekchat.utils.TimeConvert

class ChatAdapter():RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {
    private var listMessage = ArrayList<ItemMessageResponse>()

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
        var date:TextView = itemView.findViewById(R.id.itemlastmessage_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_last_message, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var message = listMessage[position]

        val date = TimeConvert.timestampToDay(message.time!!)

        holder.message.text = message.message
        holder.fullname.text = message.sendByName
        holder.date.text = date
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }
}