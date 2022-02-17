package com.example.mymessenger.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymessenger.firebase.DatabaseException
import com.example.mymessenger.firebase.FirebaseContact
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.State
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: Authenticator<@JvmSuppressWildcards FirebaseUser?>,
    private val database: DatabaseInterface
) : ViewModel() {
    private val _user = MutableSharedFlow<State<Contact>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val user: SharedFlow<State<Contact>> = _user.asSharedFlow()
    lateinit var contact:FirebaseContact
    init {
        viewModelScope.launch {
            _user.emit(State.Loading)
            try {
                val user = async { auth.requestUserInfo() }
                val avatar = async {
                    database.getFile(USER_ID)
                }
                val u = user.await()
                val a = avatar.await()
                u?.let {
                    contact = FirebaseContact(
                        uid = USER_ID,
                        email = it.email!!,
                        nickname = it.displayName!!,
                        avatar = a
                    )
                    _user.emit(
                        State.Object(
                            contact
                        )
                    )
                } ?: _user.emit(State.Error(DatabaseException(LoginViewModel.ERROR_UNKNOWN)))

            } catch (e: DatabaseException) {
                _user.emit(State.Error(e))
            }
        }
    }
    fun setAvatar(bitmap: Bitmap){
        viewModelScope.launch {
            contact = contact.copy(
                avatar = bitmapToByteArray(bitmap)
            )
            _user.emit(
                State.Object(contact)
            )
        }
    }

    fun setUserName(name: String){
        viewModelScope.launch {
            contact = contact.copy(
                nickname = name
            )
            _user.emit(
                State.Object(contact)
            )
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun saveChanges() {
        viewModelScope.launch {
            auth.updateUserInfo(contact.nickname)
            contact.avatar?.let {
                database.insertFile(it)
            }
        }
    }
}