package com.example.mymessenger.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Authenticator<Unit>
    ):ViewModel() {

    fun isDataValid(email: String, password: String): Boolean {
        return email.isValidEmail() && password.isNotEmpty()
    }

    fun signIn() = flow<State<Exception>>{
        emit(State.Success)
        try {
            auth.signIn()
            emit(State.Success)
        }catch (e: Exception){
            emit(State.Error(e))
        }
    }

    fun signOut(){
        viewModelScope.launch {
            auth.signOut()
        }
    }
}