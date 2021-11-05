package com.example.mymessenger.interfaces

import com.example.mymessenger.utills.State

interface Authenticator<out R> {
    suspend fun signIn(result: (State<R>)->Unit)
    suspend fun signOut()
}