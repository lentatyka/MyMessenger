package com.example.mymessenger.room

import androidx.room.*
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.logz
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Dao
interface RoomDao {

    //Private chat---------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: RoomMessage): Long

    @Query("DELETE FROM ${Constants.SQLITE_TABLE_CHAT} WHERE uid = :uid ")
    suspend fun deleteChat(uid: String): Int

    @Query("SELECT * FROM ${Constants.SQLITE_TABLE_CHAT} WHERE uid= :uid ORDER BY timestamp ASC")
    suspend fun getChat(uid: String):List<RoomMessage>
    @Query("SELECT username,uid,`message`,status, MAX(timestamp)as timestamp, messageId" +
            "  FROM ${Constants.SQLITE_TABLE_CHAT} GROUP BY uid")
    fun getLastMessages():Flow<List<RoomMessage>>

    @Query("UPDATE ${Constants.SQLITE_TABLE_CHAT} SET status =:status WHERE messageId =:uid")
    suspend fun updateStatus(status: MessageStatus, uid: String):Int
}