package com.example.mymessenger.ui.fragments.chatlist

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.ChatlistItemBinding
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.room.RoomLastMessage
import com.example.mymessenger.utills.*

enum class ViewType{
    CHAT, ADD_CONTACT, REMOVE_CONTACT, DELETE_CHAT
}
class ChatListAdapter(
    private val callback:(contact: Contact, ViewType)->Unit
    ):ListAdapter<RoomLastMessage,ChatListAdapter.ChatListHolder>(DiffCallback) {

    companion object{
        val DiffCallback = object : DiffUtil.ItemCallback<RoomLastMessage>() {
            override fun areItemsTheSame(oldItem: RoomLastMessage, newItem: RoomLastMessage): Boolean {
                return (oldItem.message!!.messageId == newItem.message!!.messageId) &&
                        (oldItem.contact?.isOwn == newItem.contact?.isOwn)
            }

            override fun areContentsTheSame(oldItem: RoomLastMessage, newItem: RoomLastMessage): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListHolder {
        val view = ChatlistItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ChatListHolder(view){contact, viewType1->
            callback(contact, viewType1)
        }
    }
    override fun onBindViewHolder(holder: ChatListHolder, position: Int) {
        holder.bind(
            getItem(position)
        )
    }

    class ChatListHolder(
        private val binding: ChatlistItemBinding,
        private val callback: (Contact, ViewType) -> Unit
        ):RecyclerView.ViewHolder(binding.root){
        fun bind(item: RoomLastMessage){
            binding.apply {
                Glide
                    .with(itemView)
                    .load(item.contact?.avatar)
                    .centerCrop()
                    .placeholder(R.drawable.ic_avatar)
                    .into(contactIv)
                val messageStatus = when(item.message?.status){
                    MessageStatus.SENT -> R.drawable.ic_takeoff
                    MessageStatus.DELIVERED -> R.drawable.ic_takein_delivered
                    MessageStatus.READ -> R.drawable.ic_takein_read
                    else -> null
                }
                messageStatus?.let {
                    checkStatusIv.setBackgroundResource(it)
                }
                contactNameTv.text = item.contact?.nickname
                messageTv.text = item.message?.message
                messageTv.setTypeface(
                    null,
                    if(item.message!!.status == MessageStatus.NEW) Typeface.BOLD else Typeface.NORMAL
                )
                dataTv.text = item.message.timestamp?.convertLongToTime()
                contactContainer.setOnClickListener{
                    callback(item.contact!!, ViewType.CHAT)
                }
                item.contact?.let {
                    if(!it.isOwn){
                        btnAddContact.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                callback(item.contact, ViewType.ADD_CONTACT)
                            }
                        }
                        contactContainer.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
                        )
                    }
                }
                btnDeleteChat.setOnClickListener {
                    callback(item.contact!!, ViewType.DELETE_CHAT)
                }
            }
        }
    }
}