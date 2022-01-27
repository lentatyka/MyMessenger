package com.example.mymessenger.room

import androidx.room.*
import com.example.mymessenger.utills.Constants.SQLITE_TABLE_CHAT
import com.example.mymessenger.utills.Constants.SQLITE_TABLE_CONTACTS
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.logz
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    //Private chat---------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: RoomMessage): Long

    @Query("DELETE FROM $SQLITE_TABLE_CHAT WHERE uid =:uid")
    suspend fun deleteChat(uid: String): Int

    @Query("DELETE FROM $SQLITE_TABLE_CHAT WHERE uid IN (:uid)")
    suspend fun deleteChats(uid: List<String>)

    @Query("SELECT * FROM $SQLITE_TABLE_CHAT WHERE uid= :uid  ORDER BY timestamp ASC")
    fun getChat(uid: String): Flow<List<RoomMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<RoomMessage>)

    @Query(
        "SELECT uid,`message`,status, MAX(timestamp)as timestamp, messageId" +
                "  FROM $SQLITE_TABLE_CHAT GROUP BY uid"
    )
    fun getLastMessages(): Flow<List<RoomMessage>>

    @Query("UPDATE $SQLITE_TABLE_CHAT SET status =:status WHERE messageId =:id")
    suspend fun updateStatus(status: MessageStatus?, id: String): Int


    @Query("SELECT * FROM $SQLITE_TABLE_CHAT WHERE uid = :uid AND status = 'NEW'")
    fun getNewMessages(uid: String):Flow<List<RoomMessage>>

    @Query("SELECT uid,`message`,status, MAX(timestamp)as timestamp, messageId, " +
            "$SQLITE_TABLE_CONTACTS.* FROM $SQLITE_TABLE_CHAT LEFT JOIN $SQLITE_TABLE_CONTACTS ON " +
            "$SQLITE_TABLE_CHAT.uid =  $SQLITE_TABLE_CONTACTS.cuid GROUP BY $SQLITE_TABLE_CHAT.uid")
    fun getChatList():Flow<List<RoomLastMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertContact(roomContacts: List<RoomContact>)

    @Query("SELECT * FROM $SQLITE_TABLE_CONTACTS WHERE cuid =:uid")
    suspend fun getContact(uid: String):RoomContact?

    @Query("SELECT * FROM $SQLITE_TABLE_CONTACTS WHERE isOwn = 1")
    fun getContacts():Flow<List<RoomContact>>

    @Delete
    suspend fun deleteContacts(contacts: List<RoomContact>):Int

    @Query("UPDATE $SQLITE_TABLE_CONTACTS SET isOwn = :op WHERE cuid =:uid")
    suspend fun updateContact(uid: String, op:Boolean):Int

    @Transaction
    suspend fun deleteContactAndChat(contacts: List<RoomContact>){
        deleteContacts(contacts)
        deleteChats(contacts.map { r->r.uid })
    }
    //----------------------------------------------------------------

}