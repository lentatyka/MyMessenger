package com.example.mymessenger.firebase

interface DatabaseInterface {
    suspend fun insert(message: Message)
    suspend fun delete(message: Message)
}