package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

    private val repository: DatabaseInterface
    ):ViewModel() {

    private val _contacts = MutableSharedFlow<State<List<Contact?>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val contacts:SharedFlow<State<List<Contact?>>> = _contacts.asSharedFlow()

    fun getContacts(){
        _contacts.tryEmit(State.Waiting)
        repository.getContacts{
            viewModelScope.launch {
                _contacts.emit(State.Contacts(it))
            }
        }
    }
}