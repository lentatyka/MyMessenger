package com.example.mymessenger.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.Constants.USER_NAME
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.isValidEmail
import com.example.mymessenger.utills.logz
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Authenticator<@JvmSuppressWildcards FirebaseUser?>
    ):ViewModel() {

    fun isDataValid(email: String, password: String): Boolean {
        return email.isValidEmail() && password.isNotEmpty()
    }

    fun signIn(email: String, password: String) = flow{
        emit(State.Waiting)
        try{
            val answer = auth.signIn(email, password)
            answer?.let {
                USER_NAME = answer.displayName ?: "HD"
                USER_ID = answer.uid
                emit(State.Success)
            } ?: emit(State.Error(Exception("PROBLEMA")))
        }catch (e: Exception){
            "Catch error".logz()
            emit(State.Error(e))
        }
    }

    fun isSigned():Boolean{
        auth.getUser()?.let {
            //Success
            USER_NAME = it.displayName!!
            USER_ID = it.uid
            return true
        } ?: return false
    }

    fun signOut(){
        viewModelScope.launch {
            auth.signOut()
        }
    }
}