package com.example.mymessenger.ui.fragments.contacts

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.ContactsItemBinding
import com.example.mymessenger.room.Contact

class ContactsAdapter(
    private val callback:(Contact)->Unit
    ):RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {

    var contactsList: List<Contact?> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val view = ContactsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsHolder(view){
            callback(it)
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
    fun setList(list: List<Contact?>){
        contactsList = list
        notifyDataSetChanged()
    }

    class ContactsHolder(
        private val binding: ContactsItemBinding,
        private val callback: (Contact) -> Unit
        ):RecyclerView.ViewHolder(binding.root){

        fun bind(item: Contact?){
            item?.let {
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
                    callback(item)
                }
            }
        }
    }
}