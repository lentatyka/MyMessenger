package com.example.mymessenger.room

import androidx.room.Embedded

data class RoomLastMessage(
    @Embedded
    val message: RoomMessage?,
    @Embedded
    val contact: RoomContact?
)