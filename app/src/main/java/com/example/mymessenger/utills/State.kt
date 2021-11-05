package com.example.mymessenger.utills

sealed class State<out R>{
    data class Contacts<out T>(val contacts: T):State<T>()
    data class Error(val exception: Exception):State<Nothing>()
    object Success: State<Nothing>()
    object Waiting: State<Nothing>()
}
