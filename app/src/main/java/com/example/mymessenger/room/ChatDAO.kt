package com.example.mymessenger.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mymessenger.utills.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDAO {

    @Insert
    suspend fun insert(message: Message)


    @Query("DELETE FROM ${Constants.SQLITE_TABLE_NAME} where uid = :uid ")
    suspend fun delete(uid: String)

    @Query("SELECT * FROM ${Constants.SQLITE_TABLE_NAME} WHERE uid= :uid")
    fun getChat(uid: String):Flow<List<Message>>
}