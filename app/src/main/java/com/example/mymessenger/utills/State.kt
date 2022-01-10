package com.example.mymessenger.utills

import com.example.mymessenger.firebase.DatabaseException

sealed class State<out R>{
    data class Object<out T>(val contacts: T):State<T>()
    data class Error(val error: DatabaseException):State<Nothing>()
    object Success: State<Nothing>()
    object Loading: State<Nothing>()
    object Waiting: State<Nothing>()
}
