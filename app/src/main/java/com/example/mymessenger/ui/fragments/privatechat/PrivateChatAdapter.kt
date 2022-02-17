package com.example.mymessenger.ui.fragments.privatechat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mymessenger.R
import com.example.mymessenger.databinding.MessageFromItemBinding
import com.example.mymessenger.databinding.MessageToItemBinding
import com.example.mymessenger.room.RoomMessage
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.convertLongToTime
import com.example.mymessenger.utills.logz

class PrivateChatAdapter : ListAdapter<RoomMessage, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val TYPE_FROM = 1
        private const val TYPE_TO = 2
        val DiffCallback = object : DiffUtil.ItemCallback<RoomMessage>() {
            override fun areItemsTheSame(oldItem: RoomMessage, newItem: RoomMessage): Boolean {
                return oldItem.messageId == newItem.messageId
            }

            override fun areContentsTheSame(oldItem: RoomMessage, newItem: RoomMessage): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = if (viewType == TYPE_TO) {
            MessageTo(
                MessageToItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            MessageFrom(
                MessageFromItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        //Status только у отпраленного сообщения
        return if(getItem(position).status == null)
            TYPE_FROM
        else
            TYPE_TO
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageTo -> {
                holder.bind(getItem(position))
            }
            is MessageFrom -> {
                holder.bind(getItem(position))
            }
        }

    }

    class MessageTo(val binding: MessageToItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: RoomMessage){
            binding.apply {
                messageTv.text = message.message
                when(message.status){
                    MessageStatus.SENT,
                    MessageStatus.NEW -> R.drawable.ic_takeoff
                    MessageStatus.DELIVERED -> R.drawable.ic_takein_delivered
                    MessageStatus.READ -> R.drawable.ic_takein_read
                    else -> null
                }?.let {
                    checkStatusIv.setImageResource(it)
                }
                timestampTv.text = message.timestamp!!.convertLongToTime()
            }
        }
    }

    class MessageFrom(private val binding: MessageFromItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: RoomMessage){
            binding.apply {
                messageTv.text = message.message
                timestampTv.text = message.timestamp!!.convertLongToTime()
            }
        }
    }


}