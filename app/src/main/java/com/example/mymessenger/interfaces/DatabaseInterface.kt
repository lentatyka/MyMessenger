package com.example.mymessenger.interfaces

import com.example.mymessenger.firebase.FirebaseContact
import com.example.mymessenger.utills.MessageStatus

interface DatabaseInterface {
    fun insertMessage(uid:String, message: Message)
    suspend fun deleteMessage(message: Message)
    suspend fun updateMessage(message: Message, status: MessageStatus)
    suspend fun getContacts(filter: List<String>, inclusive:Boolean):List<FirebaseContact>
    suspend fun getContact(uid: String):FirebaseContact?
    suspend fun insertFile(byteArray: ByteArray)
    suspend fun updateFile()
    suspend fun deleteFile()
    suspend fun getFile(uid: String): ByteArray?
}