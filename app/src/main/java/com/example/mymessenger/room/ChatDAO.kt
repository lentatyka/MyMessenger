package com.example.mymessenger.room

import androidx.room.*
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDAO {

    //Private chat---------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: RoomMessage): Long

//    @Query("UPDATE ${Constants.SQLITE_TABLE_CHAT} SET status =:status WHERE uid =:uid")
//    suspend fun updateMessageStatus(uid: String, status: Int):Int

    @Query("DELETE FROM ${Constants.SQLITE_TABLE_CHAT} WHERE uid = :uid ")
    suspend fun deleteChat(uid: String)

    @Query("SELECT * FROM ${Constants.SQLITE_TABLE_CHAT} WHERE uid= :uid")
    suspend fun getChat(uid: String):List<RoomMessage>
    //Private chat  END-----------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastMessage(message: RoomLastMessage):Long

    @Query("SELECT * FROM ${Constants.SQLITE_TABLE_LAST_MESSAGE} WHERE uid = :uid")
    fun getLast(uid:String):Flow<List<RoomLastMessage>>

    //CHAT LIST START-------------------------------------------------
    @Query("SELECT id,username,uid,`from`,`to`,status, MAX(timestamp)as timestamp, messageId" +
            "  FROM ${Constants.SQLITE_TABLE_CHAT} GROUP BY uid")
    fun getLastMessages():Flow<List<RoomMessage>>
    //CHAT LIST END---------------------------------------------------

    @Query("UPDATE ${Constants.SQLITE_TABLE_CHAT} SET status =:status WHERE messageId =:uid")
    suspend fun updateStatus(status: MessageStatus, uid: String):Int


}