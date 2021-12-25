package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.logz
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor():Authenticator<FirebaseUser?> {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signIn(email: String, password: String):FirebaseUser?{
        val user = auth.signInWithEmailAndPassword(email, password).await()
        return user.user
    }

    override fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}