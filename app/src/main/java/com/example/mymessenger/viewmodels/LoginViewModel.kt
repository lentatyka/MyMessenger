package com.example.mymessenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.DatabaseException
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.Constants.USER_NAME
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.isValidEmail
import com.example.mymessenger.utills.isValidPassword
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Authenticator<@JvmSuppressWildcards FirebaseUser?>
    ):ViewModel() {
    companion object{
        const val ERROR_USER_NOT_FOUND = 100
        const val ERROR_EMAIL_NOT_VERIFIED = 200
        const val ERROR_EMAIL_ALREADY_IN_USE = 300
        const val ERROR_WRONG_PASSWORD = 400
        const val ERROR_UNKNOWN = 404
    }

    fun isSignInValid(email: String, password: String): Boolean {
        return email.isValidEmail() && password.isNotEmpty()
    }

    fun signIn(email: String, password: String) = flow{
        emit(State.Loading)
        try{
            auth.signIn(email, password).also {
                USER_NAME = it!!.displayName ?: "${it.email}"
                USER_ID = it.uid
                emit(State.Success)
            }
        }catch (e: DatabaseException){
            emit(State.Error(e))
        }
    }

    fun sendEmailVerification(){
        auth.sendEmailVerification()
    }

    fun signUp(email: String, password: String, nickname: String) = flow{
        emit(State.Loading)
        try{
            auth.signUp(email, password, nickname)
            emit(State.Success)
        }catch (e: DatabaseException){
            emit(State.Error(e))
        }
    }

    fun signOut(){
        viewModelScope.launch {
            auth.signOut()
        }
    }

    fun isSignUpValid(nickname: String, email: String, password: String, cPassword: String):Boolean {
        return nickname.isNotEmpty() && email.isValidEmail() && password.isNotEmpty() && cPassword == password
    }

    fun isValidEmail(email: String): Boolean {
        return email.isValidEmail()
    }

    fun isValidPasswordOrNickname(password: String):Boolean {
        return password.trim().isNotEmpty()
    }
    fun isValidConfirmPassword(c_password: String, password:String):Boolean{
        return c_password.isNotEmpty() && c_password == password
    }

    fun resetPassword(email: String)= flow{
            try {
                auth.resetPassword(email)
                emit(State.Success)
            }catch (e:DatabaseException){
                emit(State.Error(e))
            }
    }

    fun requestUserInfo() = auth.requestUserInfo()
    fun isValidPassword(string: String) = string.isValidPassword()

}