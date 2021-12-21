package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor():Authenticator<Unit> {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun signIn(email: String, password: String) {
        val answer = auth.signInWithEmailAndPassword(email, password).await()
        //DELETE THIS TRASH!!!
        Constants.USER_ID = answer.user?.uid!!
    }
}