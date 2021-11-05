package com.example.mymessenger.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymessenger.utills.Constants

@Entity(tableName = Constants.SQLITE_TABLE_NAME)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="uid")
    val uid: String,
    @ColumnInfo(name="from")
    val message_from: String?,
    @ColumnInfo(name="to")
    val message_to: String?
)
