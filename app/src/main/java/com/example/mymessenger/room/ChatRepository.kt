package com.example.mymessenger.room

import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val roomDAO: ChatDAO
) {

    suspend fun insert(message: Message){
        roomDAO.insert(message)
    }

    suspend fun delete(uid: String){
        roomDAO.delete(uid)
    }

    fun getChat(uid: String) = roomDAO.getChat(uid)

}