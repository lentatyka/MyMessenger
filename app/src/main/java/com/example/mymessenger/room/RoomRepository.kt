package com.example.mymessenger.room

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

    suspend fun insertMessages(messages: List<RoomMessage>){
        roomDAO.insertMessages(messages)
    }

    fun getChat(uid: String) = roomDAO.getChat(uid)

    suspend fun updateStatus(message: RoomMessage){
        roomDAO.updateStatus(message.status, message.messageId)
    }

    fun getChatList() = roomDAO.getChatList()
    suspend fun insertContact(roomContact: List<RoomContact>){
        roomDAO.insertContact(roomContact)
    }
    suspend fun getContact(uid: String) = roomDAO.getContact(uid)
    fun getContacts() = roomDAO.getContacts()
    suspend fun deleteContact(contacts: List<RoomContact>) {
        roomDAO.deleteContactAndChat(contacts)
    }

}