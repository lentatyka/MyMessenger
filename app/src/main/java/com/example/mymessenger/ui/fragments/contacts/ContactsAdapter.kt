package com.example.mymessenger.ui.fragments.contacts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.ContactsItemBinding
import com.example.mymessenger.firebase.FirebaseContact
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.room.RoomContact
import com.example.mymessenger.ui.fragments.chatlist.ViewType
import kotlinx.android.synthetic.main.contacts_item.view.*

class ContactsAdapter(
    private val callback:(Contact, ViewType, Boolean)->Unit
    ):RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {

    var contactsList: List<Contact> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val view = ContactsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsHolder(view){contact, viewtype, isChanged->
            callback(contact, viewtype, isChanged)
        }
    }
    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.bind(
            contactsList[position]
        )
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Contact>){
        contactsList = list
        notifyDataSetChanged()
    }

    class ContactsHolder(
        private val binding: ContactsItemBinding,
        private val callback: (Contact, ViewType, Boolean) -> Unit
        ):RecyclerView.ViewHolder(binding.root){
        private var isChanged = false
        fun bind(item: Contact){
            item.let {
                binding.apply {
                    contactNameTv.text = item.nickname
                    emailTv.text = item.email
                    Glide.with(itemView)
                        .load(item.avatar)
                        .centerCrop()
                        .placeholder(R.drawable.ic_avatar)
                        .into(contactIv)
                }
                itemView.setOnClickListener{
                    callback(item, ViewType.CHAT, false)
                }

                itemView.setOnLongClickListener {
                    if(!isChanged){
                        binding.avatarLayout.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, android.R.color.holo_red_light)
                        )
                    }else
                        binding.avatarLayout.background = null
                    isChanged = !isChanged
                    when(item){
                        is FirebaseContact ->{
                            callback(item, ViewType.ADD_CONTACT, isChanged)
                        }
                        is RoomContact ->{
                            callback(item, ViewType.REMOVE_CONTACT, isChanged)
                        }
                    }
                    true
                }
            }
        }
    }
}