package com.example.mymessenger.firebase

import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants.USERS_PATH
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_EMAIL_ALREADY_IN_USE
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_EMAIL_NOT_VERIFIED
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_UNKNOWN
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_USER_NOT_FOUND
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_WRONG_PASSWORD
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthentication @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: StorageReference
):Authenticator<FirebaseUser?> {

    override suspend fun signIn(email: String, password: String):FirebaseUser?{
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user!!.also {
                if(!it.isEmailVerified)
                    throw DatabaseException(ERROR_EMAIL_NOT_VERIFIED)
                return result.user
            }
        }catch (e: FirebaseAuthException){
            throw DatabaseException(
                getErrorCode(e.errorCode)
            )
        }
    }
    override suspend fun signUp(
        email: String,
        password: String,
        nickname: String,
        avatar: ByteArray
    ){
        try {
            val result =  auth.createUserWithEmailAndPassword(email, password).await()
            result.user!!.also {newUser->
                UserProfileChangeRequest.Builder().setDisplayName(nickname).build().also {
                    newUser.updateProfile(it)
                    newUser.sendEmailVerification()
                }
                FirebaseDatabase.getInstance().reference
                    .child(USERS_PATH)
                    .child(newUser.uid)
                    .setValue(
                        FirebaseContact(
                            uid = newUser.uid,
                            email = newUser.email!!,
                            nickname = nickname,
                            avatar = null,
                            info = null
                        )
                    )
                storage.child(newUser.uid).putBytes(avatar)
            }
        }catch (e: FirebaseAuthException){
            throw DatabaseException(
                getErrorCode(e.errorCode)
            )
        }
    }
    override suspend fun signOut() {
        auth.signOut()
    }

    override fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
    }

    override suspend fun resetPassword(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        }catch (e: FirebaseAuthException){
            throw DatabaseException(
                getErrorCode(e.errorCode)
            )
        }
    }

    override suspend fun requestUserInfo() = auth.currentUser

    private fun getErrorCode(error: String):Int{
        val code = when(error){
            "ERROR_USER_NOT_FOUND"-> ERROR_USER_NOT_FOUND
            "ERROR_EMAIL_ALREADY_IN_USE"-> ERROR_EMAIL_ALREADY_IN_USE
            "ERROR_WRONG_PASSWORD"->ERROR_WRONG_PASSWORD
            else-> ERROR_UNKNOWN
        }
        return code
    }

    override suspend fun updateUserInfo(name: String) {
        UserProfileChangeRequest.Builder().setDisplayName(name).build().also {
            auth.currentUser?.updateProfile(it)
        }
    }
}