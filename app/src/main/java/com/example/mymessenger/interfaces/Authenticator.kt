package com.example.mymessenger.interfaces

import kotlinx.coroutines.flow.Flow


interface Authenticator<out T> {
    suspend fun signIn(email: String, password: String):T
    suspend fun signOut()
    fun getUser():T
}