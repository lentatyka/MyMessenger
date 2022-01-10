package com.example.mymessenger.room

import androidx.room.*
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.Constants.SQLITE_TABLE_CHAT
import com.example.mymessenger.utills.Constants.SQLITE_TABLE_CONTACTS
import com.example.mymessenger.utills.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    //Private chat---------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: RoomMessage): Long

    @Query("DELETE FROM $SQLITE_TABLE_CHAT WHERE uid =:uid")
    suspend fun deleteChat(uid: String): Int

    @Query("SELECT * FROM $SQLITE_TABLE_CHAT WHERE uid= :uid AND status IS NOT 'NEW'  ORDER BY timestamp ASC")
    fun getChat(uid: String): Flow<List<RoomMessage>>

    @Query(
        "SELECT username,uid,`message`,status, MAX(timestamp)as timestamp, messageId" +
                "  FROM $SQLITE_TABLE_CHAT GROUP BY uid"
    )
    fun getLastMessages(): Flow<List<RoomMessage>>

    @Query("UPDATE $SQLITE_TABLE_CHAT SET status =:status WHERE messageId =:id")
    suspend fun updateStatus(status: MessageStatus?, id: String): Int

    @Update
    suspend fun updateStatus(messages: List<RoomMessage>):Int

    @Query("SELECT * FROM $SQLITE_TABLE_CHAT WHERE uid = :uid AND status = 'NEW'")
    fun getNewMessages(uid: String):Flow<List<RoomMessage>>
    //----------------------------------------------------------------

}