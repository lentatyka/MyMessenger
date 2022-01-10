package com.example.mymessenger.utills

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.mymessenger.R
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.room.Contact
import com.example.mymessenger.room.RoomMessage
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.regex.Pattern

fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun CharSequence?.isValidPassword():Boolean{
    if(isNullOrEmpty())
        return false
    val PASSWORD_PATTERN = "^(?=.*[A-Za-z_0-9])(?=\\S+$).{8,}$"
    return Pattern.compile(PASSWORD_PATTERN).matcher(this).matches()
}

fun CharSequence.showSnackBar(view: View, action: ()->Unit){
    Snackbar.make(view, this, Snackbar.LENGTH_INDEFINITE).setAction(R.string.apply){
        action()
    }.show()
}

fun CharSequence.showToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun TextInputLayout.setListener(errorMessage: String, field: TextInputEditText, validator: () -> Boolean){
    field.setOnFocusChangeListener { _, hasFocus ->
        if(hasFocus){
            this.error = null
        }
        else{
            if(!validator()){
                field.setText(field.text.toString().trim())
                this.error = errorMessage
            }
        }
    }
}

fun Long.convertLongToTime() = SimpleDateFormat("h:mm a").format(this).toString()

fun <T: Message> T.remoteMessageToSqlite():RoomMessage{
    return RoomMessage(
        uid = this.uid,
        name = this.name,
        message = this.message,
        status = status,
        timestamp = this.timestamp,
        messageId = this.messageId
    )
}

fun <T: Message>T.messageToContact(): Contact =
    Contact(
        uid = this.uid,
        nickname = this.name,
        info = this.message
    )

fun getCurrentTime():Long = System.currentTimeMillis()

fun <T> Flow<T>.launchWhenCreated(lifecycleScope: LifecycleCoroutineScope){
    lifecycleScope.launchWhenCreated {
        this@launchWhenCreated.collect()
    }
}

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope){
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}

fun <T> Flow<T>.launchWhenResumed(lifecycleScope: LifecycleCoroutineScope){
    lifecycleScope.launchWhenResumed {
        this@launchWhenResumed.collect()
    }
}

fun String.logz(){
    Log.d("TAG", this)
}