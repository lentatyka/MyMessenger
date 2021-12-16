package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.FirebaseMessage
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.room.ChatRepository
import com.example.mymessenger.utills.*
import com.xwray.groupie.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: DatabaseInterface,
    private val roomRepository: ChatRepository
) : ViewModel() {

    //chatlist------------------------------------------------------
    private val _lastMessages = MutableStateFlow<List<Message>>(emptyList())
    val lastMessages: StateFlow<List<Message>> get() = _lastMessages.asStateFlow()

    val _dudla = MutableStateFlow<String>("caller")
    //--------------------------------------------------------------

    fun getContacts() = flow {
        emit(State.Waiting)
        try {
            val contacts = (remoteRepository as FirebaseRepository).getContacts()
            emit(State.Contacts(contacts))
        } catch (e: Exception) {
            emit(State.Error(e))
        }
    }
    fun updateLastMessage() {
        //Отработаем сообщение только если активность запущена
        viewModelScope.launch {
                roomRepository.getLastMessages().collect{
                    _lastMessages.emit(it)
                }
        }
    }

//    fun getPrivateChat(uid: String) {
//        viewModelScope.launch {
//            privateChatList.clear()
//            roomRepository.getChat(uid).also {messageList->
//                messageList.forEach {
//                    privateChatList +=
//                        if(it.from != null)
//                            MessageFrom(it)
//                        else
//                            MessageTo(it)
//                }
//                _privateChat.emit(privateChatList)
//            }
//        }
//    }
}