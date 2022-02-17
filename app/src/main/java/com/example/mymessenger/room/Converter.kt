package com.example.mymessenger.room

import androidx.room.TypeConverter
import com.example.mymessenger.utills.MessageStatus

class Converter {

    @TypeConverter
    fun toMessageStatus(value: Int) = enumValues<MessageStatus>()[value]

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus) = value.ordinal
}