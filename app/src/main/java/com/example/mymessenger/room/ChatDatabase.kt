package com.example.mymessenger.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class ChatDatabase:RoomDatabase() {
    abstract fun getChatDAO():ChatDAO
}