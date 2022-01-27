package com.example.mymessenger.utills

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.firebase.FirebaseContact
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.room.RoomContact
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
        message = this.message,
        status = status,
        timestamp = this.timestamp,
        messageId = this.messageId
    )
}

fun <T: Message>T.messageToContact():FirebaseContact =
    FirebaseContact(
        uid = this.uid,
        email = "",
        avatar = null,
        info = this.message
    )

fun <T: Contact>T.contactToRoomContact():RoomContact =
    RoomContact(
        uid = this.uid!!,
        nickname = this.nickname!!,
        email = this.email!!,
        avatar = this.avatar,
        info = null
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

@BindingAdapter("view_set")
fun setVisibility(view: View, state: State<Any>){
    if(state is State.Loading){
        view.visibility = View.VISIBLE
    }
    else
        view.visibility = View.GONE
}

@BindingAdapter("avatar_bind", "error")
fun loadAvatar(image: ImageView, bytes: ByteArray?, error: Drawable){
    Glide.with(image)
        .load(bytes)
        .error(error)
        .placeholder(error)
        .into(image)
}

fun String.logz(){
    Log.d("TAG", this)
}