package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.DatabaseException
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.repository.Repository
import com.example.mymessenger.room.RoomContact
import com.example.mymessenger.room.RoomMessage
import com.example.mymessenger.utills.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    //--ChatList Session
    fun getChatList() = flow {
        repository.getChatListLocal().collect {
            it.filter {c-> c.contact == null }.also {list->
                if(list.isNotEmpty()) {
                    val contacts =
                        repository.getContactRemote(list.map { c -> c.message!!.uid }, true)
                    val roomContacts = contacts.map { contact ->
                        RoomContact(
                            uid = contact.uid,
                            email = contact.email,
                            nickname = contact.nickname,
                            avatar = contact.avatar!!,
                            info = null,
                            isOwn = false
                        )
                    }
                    repository.insertContactLocal(roomContacts)
                }
            }
            emit(it)
        }
    }

    fun deleteChat(uid: String) {
        viewModelScope.launch {
            repository.deleteChatLocal(uid)
        }
    }
    //--End ChatList Session

    //--Contact Session
    fun getFriendList() = repository.getFriendListLocal()
    //--End Contact Session

    //--PrivateChat Session
    fun getChat(uid: String) = flow {
        repository.getChatLocal(uid).collect {
            repository.sendMessageStatus(it.filter { rm -> rm.status == MessageStatus.NEW })
            emit(it)
        }
    }

    private fun getRoomMessageEntry(message: String, contact: Contact) = RoomMessage(
        uid = contact.uid!!,
        message = message,
        status = MessageStatus.SENT,
        timestamp = getCurrentTime(),
        messageId = UUID.randomUUID().toString()
    )

    fun isMessageValid(message: CharSequence?): Boolean {
        return message?.trim()!!.isNotBlank()
    }

    fun sendMessage(text: String, contact: Contact) {
        viewModelScope.launch {
            val message = getRoomMessageEntry(text.trim(), contact)
            repository.sendMessage(message)
        }
    }
    //--End PrivateChat Session

    //--Contacts
    fun addContactToFriend(contacts: List<Contact>) {
        val roomContact = contacts.map {
            RoomContact(
                uid = it.uid!!,
                email = it.email!!,
                nickname = it.nickname!!,
                avatar = it.avatar!!,
                info = null,
                isOwn = true
            )
        }
        viewModelScope.launch {
            repository.insertContactLocal(roomContact)
        }
    }

    fun findContacts() = flow {
        emit(State.Loading)
        try {
            repository.findContactsRemote().collect {
                emit(State.Object(it))
            }
        } catch (e: DatabaseException) {
            emit(State.Error(e))
        }
    }

    fun deleteContacts(contacts: List<RoomContact>) {
        viewModelScope.launch {
            repository.deleteContactLocal(contacts)
        }
    }
    //--End Contacts
}