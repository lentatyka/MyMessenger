package com.example.mymessenger.interfaces

import com.example.mymessenger.utills.MessageStatus

interface Message {
    val uid: String
    val message: String?
    val status: MessageStatus?
    val timestamp: Long?
    val messageId: String
}