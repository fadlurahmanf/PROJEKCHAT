package com.example.projekchat.ui.home.ui.searchfriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekchat.R
import com.example.projekchat.response.SearchResponse
import com.example.projekchat.response.UserResponse

class SearchFriendAdapter(val listUserResponse: ArrayList<SearchResponse>):RecyclerView.Adapter<SearchFriendAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback:OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data:SearchResponse)
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
        holder.fullname_text.text = user.friendFullname
        if (user.friendStatus!="null"){
            holder.status_text.text = user.friendStatus
        }
        if (user.friendProfile!="null"){
            Glide.with(holder.imageProfile).load(user.friendProfile).into(holder.imageProfile)
        }

        if (user.status==0){
            holder.friendStatus.setImageResource(R.drawable.ic_person_add_black)
            holder.friendStatus.setOnClickListener{
                holder.friendStatus.setImageResource(R.drawable.ic_person_black)
                onItemClickCallback.onItemClicked(listUserResponse[holder.adapterPosition])
            }
        }else if (user.status==1){
            holder.friendStatus.setImageResource(R.drawable.ic_person_black)
        }
    }

    override fun getItemCount(): Int {
        return listUserResponse.size
    }
}