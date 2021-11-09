package com.example.mymessenger.interfaces


interface Authenticator<out T> {
    suspend fun signIn():T
    suspend fun signOut()
}