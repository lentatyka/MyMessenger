package com.example.mymessenger.interfaces

import com.example.mymessenger.firebase.FirebaseMessage
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.MessageStatus

interface DatabaseInterface {
    suspend fun insert(message: Message)
    suspend fun delete(message: Message)
    suspend fun updateStatus(message: Message, status: MessageStatus)
}