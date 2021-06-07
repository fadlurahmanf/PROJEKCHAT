package com.example.projekchat.ui.home.ui.searchfriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekchat.R
import com.example.projekchat.response.UserResponse

class SearchFriendAdapter(val listUserResponse: ArrayList<UserResponse>):RecyclerView.Adapter<SearchFriendAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback:OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data:UserResponse)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var fullname_text:TextView = itemView.findViewById(R.id.itemFriend_fullname)
        var status_text:TextView = itemView.findViewById(R.id.itemFriend_status)
        var friendStatus:ImageView = itemView.findViewById(R.id.itemFriend_friendStatus)
        var imageProfile:ImageView = itemView.findViewById(R.id.itemFriend_imageProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_friend, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var user = listUserResponse[position]
        holder.fullname_text.text = user.fullName
        holder.status_text.text = user.status
        Glide.with(holder.imageProfile).load("").into(holder.imageProfile)

        holder.friendStatus.setOnClickListener{
            holder.friendStatus.setImageResource(R.drawable.ic_person_black)
            onItemClickCallback.onItemClicked(listUserResponse[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listUserResponse.size
    }
}