package com.example.mymessenger.room

data class Chat<T>(
    val messages: MutableList<T>
)
