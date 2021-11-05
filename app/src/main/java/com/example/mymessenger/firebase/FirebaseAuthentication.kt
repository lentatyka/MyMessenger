package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.State
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor():Authenticator<@JvmSuppressWildcards Nothing> {
    private val auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun signIn(result: (State<Nothing>) -> Unit) {
        auth.signInWithEmailAndPassword(Constants.EMAIL, Constants.PASSWORD)
            .addOnSuccessListener {
                result(State.Success)
            }
            .addOnFailureListener {
                result(State.Error(it))
            }
    }
}