package com.example.mymessenger.utills

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.room.RoomMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat

fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun Long.convertLongToTime() = SimpleDateFormat("h:mm a").format(this).toString()

fun <T: Message> T.remoteMessageToSqlite():RoomMessage{
    return RoomMessage(
        uid = this.uid!!,
        name = this.name!!,
        message = this.message,
        status = this.status,
        timestamp = this.timestamp,
        messageId = this.messageId!!
    )
}

fun <T: Message>T.messageToContact():Contact =
    Contact(
        uid = this.uid,
        nickname = this.name
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