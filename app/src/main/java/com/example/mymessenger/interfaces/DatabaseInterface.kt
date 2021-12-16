package com.example.mymessenger.interfaces

import com.example.mymessenger.firebase.FirebaseMessage
import com.example.mymessenger.utills.Contact

interface DatabaseInterface {
    suspend fun insert(message: Message):String
    suspend fun delete(message: Message)
    suspend fun updateStatus(message: Message)
}