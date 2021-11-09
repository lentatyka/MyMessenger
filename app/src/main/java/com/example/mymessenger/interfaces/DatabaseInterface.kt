package com.example.mymessenger.interfaces

import com.example.mymessenger.firebase.MessageFB
import com.example.mymessenger.utills.Contact
import kotlinx.coroutines.flow.Flow

interface DatabaseInterface {
    suspend fun insert(message: MessageFB)
    suspend fun delete(message: MessageFB)
}