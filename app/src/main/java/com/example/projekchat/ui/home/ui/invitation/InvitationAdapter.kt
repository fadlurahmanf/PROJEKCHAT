package com.example.projekchat.ui.home.ui.invitation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekchat.R
import com.example.projekchat.response.UserResponse
import com.google.android.material.imageview.ShapeableImageView

class InvitationAdapter():RecyclerView.Adapter<InvitationAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback:OnItemClickCallback
    private var mapUser = HashMap<String, UserResponse>()
    private var listInvitation = ArrayList<UserResponse>()

    fun setMapUser(map:HashMap<String, UserResponse>){
        if (map!=null){
            mapUser = map
        }
    }

    fun setListUser(list:List<UserResponse>){
        if (list!=null){
            listInvitation.clear()
            listInvitation.addAll(list)
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(userResponseFriend: UserResponse)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var fullName_text:TextView = itemView.findViewById(R.id.itemInvitation_fullName)
        var image:ShapeableImageView = itemView.findViewById(R.id.itemInvitation_image)
        var icon_accepted:ImageView = itemView.findViewById(R.id.itemInvitation_acceptInvitation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invitation, parent, false)
        return ListViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var user = listInvitation[position]

        holder.fullName_text.text = user.fullName
        if (user.imageProfile.toString()!="null"){
            Glide.with(holder.image).load(user.imageProfile).into(holder.image)
        }
        holder.icon_accepted.setOnClickListener {
            onItemClickCallback.onItemClicked(listInvitation[holder.adapterPosition])
            listInvitation.remove(listInvitation[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return listInvitation.size
    }
}