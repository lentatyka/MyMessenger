package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.room.RoomRepository
import com.example.mymessenger.room.RoomMessage
import com.example.mymessenger.utills.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: DatabaseInterface,
    private val roomRepository: RoomRepository
) : ViewModel() {

    fun getContacts() = flow {
        emit(State.Waiting)
        try {
            val contacts = (remoteRepository as FirebaseRepository).getContacts()
            emit(State.Contacts(contacts))
        } catch (e: Exception) {
            emit(State.Error(e))
        }
    }
    fun getChatList() = roomRepository.getLastMessages()

    fun sendMessage(text: String, contact: Contact) {
        viewModelScope.launch {
            val message = getRoomMessageEntry(text, contact)
            roomRepository.insertMessage(message)
            remoteRepository.insert(message)
        }
    }

    fun getPrivateChat(uid: String) =flow{
        roomRepository.getChat(uid).collect {list->
            emit(list)
            list.filter {rm->
                rm.status != MessageStatus.READ && rm.status != null
            }.onEach { message->
                "oneach: $message".logz()
                //remoteRepository.updateStatus(message, MessageStatus.READ)
            }
        }
    }


    private fun getRoomMessageEntry(message: String, contact: Contact) = RoomMessage(
        uid = contact.uid!!,
        name = contact.nickname!!,
        message = message,
        status = MessageStatus.SENT,
        timestamp = getCurrentTime(),
        messageId = UUID.randomUUID().toString()
    )

}