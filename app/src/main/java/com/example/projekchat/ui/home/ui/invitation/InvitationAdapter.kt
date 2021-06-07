package com.example.projekchat.ui.home.ui.invitation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekchat.R

class InvitationAdapter(var listUser:ArrayList<String>):RecyclerView.Adapter<InvitationAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback:OnItemClickCallback
    interface OnItemClickCallback{
        fun onItemClicked(user:String, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var fullName_text:TextView = itemView.findViewById(R.id.itemInvitation_fullName)
        var icon_accepted:ImageView = itemView.findViewById(R.id.itemInvitation_acceptInvitation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invitation, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var user = listUser[position]

        holder.fullName_text.text = user
        holder.icon_accepted.setOnClickListener {
//            listUser.removeAt(position)
//            notifyItemRemoved(position)
//            notifyDataSetChanged()
            onItemClickCallback.onItemClicked(listUser[holder.adapterPosition], position)
        }
    }

    override fun getItemCount(): Int {
        return listUser.size
    }
}