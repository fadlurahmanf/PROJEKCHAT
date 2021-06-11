package com.example.projekchat.ui.home.ui.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R
import com.example.projekchat.response.UserResponse

class FriendAdapter(val arrayList: ArrayList<UserResponse>):RecyclerView.Adapter<FriendAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback:OnItemClickCallback

    interface OnItemClickCallback{
        fun onClicked(data:UserResponse)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var fullname_text:TextView = itemView.findViewById(R.id.itemFriend_fullname)
        var imageprofile:ImageView = itemView.findViewById(R.id.itemFriend_imageProfile)
        var status:TextView = itemView.findViewById(R.id.itemFriend_status)
        var friendstatus:ImageView = itemView.findViewById(R.id.itemFriend_friendStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_friend, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var user = arrayList[position]

        holder.fullname_text.text = user.fullName
        holder.status.text = user.status
        holder.friendstatus.visibility = View.INVISIBLE

        holder.itemView.setOnClickListener { onItemClickCallback.onClicked(user) }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}