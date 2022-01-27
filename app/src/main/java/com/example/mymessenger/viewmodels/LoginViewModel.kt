package com.example.mymessenger.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.R
import com.example.mymessenger.firebase.DatabaseException
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.isValidEmail
import com.example.mymessenger.utills.isValidPassword
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: Authenticator<@JvmSuppressWildcards FirebaseUser?>,
    val app: Application
    ):AndroidViewModel(app) {
    private var avatar: Bitmap
    private val _state = MutableStateFlow<State<Nothing>>(State.Waiting)
    val state:StateFlow<State<Nothing>> = _state.asStateFlow()
    init {
        avatar = ResourcesCompat
            .getDrawable(app.resources, R.drawable.ic_avatar, app.theme)!!
            .toBitmap()
    }
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

    fun signIn(email: String, password: String){
        viewModelScope.launch {
            _state.value = State.Loading
            try{
                auth.signIn(email, password).also {
                    USER_ID = it?.uid!!
                    _state.value = State.Success
                }
            }catch (e: DatabaseException){
                _state.value = State.Error(e)
            }
        }
    }

    fun sendEmailVerification(){
        auth.sendEmailVerification()
    }

    fun signUp(email: String, password: String, nickname: String){
        viewModelScope.launch {
            _state.value = State.Loading
            try{
                val stream = ByteArrayOutputStream()
                avatar.compress(Bitmap.CompressFormat.PNG, 100, stream)
                auth.signUp(email, password, nickname, stream.toByteArray())
                _state.value = State.Success
            }catch (e: DatabaseException){
                _state.value = State.Error(e)
            }
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
    fun isValidPassword(string: String) = string.isValidPassword()
    fun setAvatar(it: Bitmap) {
        avatar = it
    }

    fun getAvatar() = avatar
    fun setState(state: State.Waiting) {
        _state.value = state
    }

}