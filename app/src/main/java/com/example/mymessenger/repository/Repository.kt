package com.example.mymessenger.repository

import com.example.mymessenger.firebase.FirebaseMessage
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.room.RoomContact
import com.example.mymessenger.room.RoomMessage
import com.example.mymessenger.room.RoomRepository
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.remoteMessageToSqlite
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteRepository: DatabaseInterface,
    private val roomRepository: RoomRepository
) {

    //--Remote database
    private fun getFirebaseMessageEntry(
        uid: String,
        message: String?,
        status: MessageStatus?,
        timestamp: Long?,
        messageId: String
    ) = FirebaseMessage(
        uid,
        message,
        status,
        timestamp,
        messageId
    )

    fun getChatListenerRemote(callback: (Message) -> Unit) =
        (remoteRepository as FirebaseRepository).addChatListener() {
            callback(it)
        }

    fun removeChatListenerRemote() {
        (remoteRepository as FirebaseRepository).removeChatListener()
    }

    fun findContactsRemote() =flow{
        getFriendListLocal().collect {
            emit(
                getContactRemote(it.map { c->c.uid }, false)
            )
        }
    }

    suspend fun getContactRemote(filter: List<String>, inclusive: Boolean) = remoteRepository.getContacts(filter, inclusive)
    //--End remote database

    //--Local database
    fun getFriendListLocal() = roomRepository.getContacts()

    suspend fun checkContactLocal(uid: String) = roomRepository.getContact(uid)

    fun getChatLocal(uid: String) = roomRepository.getChat(uid)

    suspend fun deleteChatLocal(uid: String) {
        roomRepository.deleteChat(uid)
    }

    fun getChatListLocal() = roomRepository.getChatList()

    suspend fun insertContactLocal(roomContacts: List<RoomContact>){
        if(roomContacts.isNotEmpty())
            roomRepository.insertContact(roomContacts)
    }
    suspend fun deleteContactLocal(contacts: List<RoomContact>) {
        roomRepository.deleteContact(contacts)
    }
    //--End local database

    //--Incoming messages
    suspend fun receiveMessage(message: Message) {
        //Отправь статус доставлено на сервер
        remoteRepository.insertMessage(
            message.uid,
            getFirebaseMessageEntry(
                USER_ID, null,  MessageStatus.DELIVERED, null, message.messageId
            )
        )
        //Удалить на сервере текущее сообщение
        remoteRepository.deleteMessage(message)
        // Ну и запиши локально текущее сообщение
        roomRepository.insertMessage(message.remoteMessageToSqlite())
    }

    suspend fun receiveMessageStatus(message: Message) {
        roomRepository.updateStatus(message.remoteMessageToSqlite())
        if(message.status == MessageStatus.READ)
            remoteRepository.deleteMessage(message)
    }
    //--End incoming messages

    //--Outgoing messages
    suspend fun sendMessage(message: RoomMessage) {
        roomRepository.insertMessage(message)
        remoteRepository.insertMessage(
            message.uid,
            FirebaseMessage(
                uid = USER_ID,
                status = MessageStatus.NEW,
                timestamp = message.timestamp,
                messageId = message.messageId,
                message = message.message
            )
        )
    }
    suspend fun sendMessageStatus(newMessages: List<RoomMessage>) {
        if(newMessages.isNotEmpty()){
            val list = mutableListOf<RoomMessage>()
            newMessages.forEach {
                remoteRepository.insertMessage(
                    it.uid,
                    getFirebaseMessageEntry(
                        USER_ID, null, MessageStatus.READ, null, it.messageId)
                )
                list += it.copy(
                    status = null
                )
            }
            roomRepository.insertMessages(list)
        }
    }

    suspend fun checkUndeliveredMessages() {

    }

    //--End outgoing messages


}