package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.room.Chat
import com.example.mymessenger.room.ChatRepository
import com.example.mymessenger.room.Message
import com.example.mymessenger.utills.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DatabaseInterface,
    private val sqliteRepository: ChatRepository
) : ViewModel() {
    private val _chat = MutableSharedFlow<Chat<Message>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val chat: SharedFlow<Chat<Message>> get() = _chat.asSharedFlow()

    /*
        НЕ РАССМАТРИВАЕМ НАЛИЧИЕ ПОДКЛЮЧЕНИЯ К ИНТЕРНЕТУ, Т.К. МЕССЕНДЖЕР ДЛЯ ПРИМЕРА РАБОТАЕТ С
        КОНТАКТАМИ, СОЗДАННЫМИ В РУЧНУЮ, А НЕ ИЗ СПИСКА ВАШИХ КОНТАКТОВ, КАК WHATSAPP ИЛИ ТЕЛЕГА
        МОЖНО РЕАЛИЗОВАТЬ АВТОРИЗАЦИЮ ЧЕРЕЗ GOOGLE ACCOUNT, НО... ЛЕНЬ =)
        */
    fun getContacts() = flow{
        emit(State.Waiting)
        try {
            val contacts = (repository as FirebaseRepository).getContacts()
            emit(State.Contacts(contacts))
        }catch (e: Exception){
            emit(State.Error(e))
        }
    }

    /*
    FIREBASE ИСПОЛЬЗУЕТСЯ КАК ПОСРЕДНИК МЕЖДУ ПОЛЬЗОВАТЕЛЯМИ. КАК ТОЛЬКО ОДИН ПОЛУЧИЛ СООБЩЕНИЕ,
    А ДРУГОЙ УВЕДОМЛЕН О ПРОЧТЕНИИ, СООБЩЕНИЯ С СЕРВЕРА УДАЛЯЮТСЯ. ПОЛНАЯ ПЕРЕПИСКА ХРАНИТСЯ НА ТЕЛЕФОНЕ.
    */
    fun getChat(uid: String) {
        viewModelScope.launch {
            sqliteRepository.getChat("100500").collect {
                _chat.emit(
                    Chat(it.toMutableList())
                )
            }
        }
    }
    fun testing(){
        (repository as FirebaseRepository).addChatListener()
    }
}