package com.example.mymessenger.firebase

import android.util.Log
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants
import com.google.firebase.auth.FirebaseAuth
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
        val answer = auth.signInWithEmailAndPassword(Constants.EMAIL, Constants.PASSWORD).await()
        //DELETE THIS TRASH!!!
        Constants.USER_ID = answer.user?.uid!!
        Log.d("TAG", Constants.USER_ID)
    }
}