package com.example.mymessenger.room

import com.example.mymessenger.utills.logz
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val roomDAO: RoomDao
) {

    suspend fun insertMessage(message: RoomMessage):Long{
        return roomDAO.insertMessage(message)
    }

    suspend fun deleteChat(uid: String){
        roomDAO.deleteChat(uid)
    }

    fun getLastMessages() = roomDAO.getLastMessages()

    fun getNewMessages(uid: String) = roomDAO.getNewMessages(uid)

    fun getChat(uid: String) = roomDAO.getChat(uid)

    suspend fun updateStatus(message: RoomMessage){
        roomDAO.updateStatus(message.status, message.messageId)
    }

    suspend fun updateStatus(messages: List<RoomMessage>){
        roomDAO.updateStatus(messages)
    }
}