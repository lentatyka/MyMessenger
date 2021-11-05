package com.example.mymessenger.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mymessenger.utills.Constants

@Dao
interface ChatDAO {

    @Insert
    suspend fun insert(message: Message)

    @Delete
    suspend fun delete(chat: Chat)

    @Query("SELECT * FROM ${Constants.SQLITE_TABLE_NAME} WHERE uid= :uid")
    suspend fun getChat(uid: String)
}