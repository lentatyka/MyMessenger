package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mymessenger.firebase.AuthenticationException
import com.example.mymessenger.repository.Repository
import com.example.mymessenger.utills.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    fun getContacts() = flow {
        emit(State.Loading)
        try {
            val contacts = repository.getContacts()
            emit(State.Object(contacts))
        } catch (e: AuthenticationException) {
            emit(State.Error(e))
        }
    }
    fun getChatList() = repository.getLastMessages()

}