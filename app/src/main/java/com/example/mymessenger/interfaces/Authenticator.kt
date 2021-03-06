package com.example.mymessenger.interfaces

import kotlinx.coroutines.flow.Flow

interface Authenticator<out T> {
    suspend fun signIn(email: String, password: String):T
    suspend fun signUp(email: String, password: String, nickname: String, avatar: ByteArray)
    suspend fun signOut()
    fun sendEmailVerification()
    suspend fun resetPassword(email: String)
    suspend fun requestUserInfo(): T
    suspend fun updateUserInfo(name: String)
}