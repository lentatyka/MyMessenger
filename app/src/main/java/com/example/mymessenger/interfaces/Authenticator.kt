package com.example.mymessenger.interfaces

import kotlinx.coroutines.flow.Flow

interface Authenticator<out T> {
    suspend fun signIn(email: String, password: String):T
    suspend fun signUp(email: String, password: String, nickname: String)
    suspend fun signOut()
    fun sendEmailVerification()
    suspend fun resetPassword(email: String)
    fun requestUserInfo(): Flow<T>
}