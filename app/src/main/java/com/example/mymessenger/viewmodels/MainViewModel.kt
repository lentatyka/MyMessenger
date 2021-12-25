package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.repository.Repository
import com.example.mymessenger.room.RoomRepository
import com.example.mymessenger.room.RoomMessage
import com.example.mymessenger.utills.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun getContacts() = flow {
        emit(State.Waiting)
        try {
            val contacts = repository.getContacts()
            emit(State.Contacts(contacts))
        } catch (e: Exception) {
            emit(State.Error(e))
        }
    }
    fun getChatList() = repository.getLastMessages()

}