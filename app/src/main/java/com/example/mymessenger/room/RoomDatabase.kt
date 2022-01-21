package com.example.mymessenger.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomMessage::class, RoomContact::class], version = 1, exportSchema = false)
abstract class RoomDatabase:RoomDatabase() {
    abstract fun getChatDAO():RoomDao
}