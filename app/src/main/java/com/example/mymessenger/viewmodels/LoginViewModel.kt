package com.example.mymessenger.viewmodels

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
    private val auth: Authenticator<@JvmSuppressWildcards Nothing>
    ):ViewModel() {

    private val _authState = MutableStateFlow<State< Nothing>>(State.Success)
    val authState: StateFlow<State<Nothing>> = _authState.asStateFlow()

    fun isDataValid(email: String, password: String): Boolean {
        return email.isValidEmail() && password.isNotEmpty()
    }

    fun signIn() {
        //Что тут творится!!!!!!!
            viewModelScope.launch {
                auth.signIn {
                    _authState.value = it
                }
            }
    }

    fun signOut(){
        viewModelScope.launch {
            auth.signOut()
        }
    }
}