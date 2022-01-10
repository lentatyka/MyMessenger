package com.example.mymessenger.interfaces

import android.net.Uri
import com.example.mymessenger.room.Contact
import com.example.mymessenger.utills.MessageStatus

interface DatabaseInterface {
    suspend fun insertMessage(uid:String, message: Message)
    suspend fun deleteMessage(message: Message)
    suspend fun updateMessage(message: Message, status: MessageStatus)
    suspend fun getContacts():List<Contact>
    suspend fun insertFile()
    suspend fun updateFile()
    suspend fun deleteFile()
    suspend fun getFile(uid: String): ByteArray?
    fun loadAvatar(uri: String, callback: (ByteArray?)->Unit)
}