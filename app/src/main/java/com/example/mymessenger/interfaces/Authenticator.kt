package com.example.mymessenger.interfaces


interface Authenticator<out T> {
    suspend fun signIn(email: String, password: String):T
    suspend fun signOut()
}