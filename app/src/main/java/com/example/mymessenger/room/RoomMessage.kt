package com.example.mymessenger.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.utills.MessageStatus

@Entity(tableName = Constants.SQLITE_TABLE_CHAT)
data class RoomMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    @ColumnInfo(name = "username")
    override val name: String?,
    @ColumnInfo(name="uid")
    override val uid: String?,
    @ColumnInfo(name="from")
    override val from: String?,
    @ColumnInfo(name="to")
    override val to: String?,
    @ColumnInfo(name="status")
    override val status: MessageStatus?,
    @ColumnInfo(name = "timestamp")
    override val timestamp: Long?,
    @ColumnInfo(name = "messageId")
    override val messageId: String?
):Message
