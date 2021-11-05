package com.example.mymessenger.room

import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val roomDAO: ChatDAO
) {

    suspend fun insert(message: Message){
        roomDAO.insert(message)
    }

    suspend fun delete(chat: Chat){
        roomDAO.delete(chat)
    }

    suspend fun getChat(uid: String){
        roomDAO.getChat(uid)
    }
}