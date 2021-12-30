package com.example.mymessenger.utills

import com.example.mymessenger.firebase.AuthenticationException
import kotlin.Exception

sealed class State<out R>{
    data class Object<out T>(val contacts: T):State<T>()
    data class Error(val error: AuthenticationException):State<Nothing>()
    object Success: State<Nothing>()
    object Loading: State<Nothing>()
    object Waiting: State<Nothing>()
}
