package com.example.mymessenger.room

import com.example.mymessenger.utills.logz
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val roomDAO: ChatDAO
) {

    suspend fun insertMessage(message: RoomMessage):Long{
        return roomDAO.insertMessage(message)
    }

    suspend fun deleteChat(uid: String){
        roomDAO.deleteChat(uid)
    }

    fun getLastMessages() = roomDAO.getLastMessages()

    suspend fun getChat(uid: String) = roomDAO.getChat(uid)

    fun getLast(uid:String) = roomDAO.getLast(uid)

    suspend fun insertLastMessage(msg: RoomLastMessage){
        val answer = roomDAO.insertLastMessage(msg)
        "insert LAst?: $answer".logz()
    }

    suspend fun updateStatus(message: RoomMessage){
        roomDAO.updateStatus(message.status!!, message.messageId!!)
    }
}