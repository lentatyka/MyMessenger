package com.example.mymessenger.ui.fragments.login

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.AvatarItemBinding

class AvatarAdapter(
    private val list:MutableList<Bitmap?>,
    private val callback:(Bitmap?)->Unit
):RecyclerView.Adapter<AvatarAdapter.AvatarHolder>(){

    class AvatarHolder(
        val binding: AvatarItemBinding,
        val callback: (Bitmap?) -> Unit
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(item: Bitmap?){
           binding.avatarIv.setOnClickListener {
                callback(item)
            }
            Glide.with(itemView)
                .load(item)
                .placeholder(R.drawable.ic_add_avatar)
                .centerCrop()
                .into(binding.avatarIv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarHolder {
        val view = AvatarItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AvatarHolder(view){
            callback(it)
        }
    }

    override fun onBindViewHolder(holder: AvatarHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}