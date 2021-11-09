package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor():Authenticator<Unit> {
    private val auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun signIn() {
        auth.signInWithEmailAndPassword(Constants.EMAIL, Constants.PASSWORD).await()
    }
}