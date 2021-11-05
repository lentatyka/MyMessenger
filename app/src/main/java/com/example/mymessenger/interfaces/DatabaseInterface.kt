package com.example.mymessenger.interfaces

import com.example.mymessenger.firebase.Message
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.State

interface DatabaseInterface {
    suspend fun insert(message: Message)
    suspend fun delete(message: Message)
    fun getContacts(callback:(List<Contact?>)->Unit)
}