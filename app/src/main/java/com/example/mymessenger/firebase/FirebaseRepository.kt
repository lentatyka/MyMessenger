package com.example.mymessenger.firebase


import android.util.Log
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.Constants.USER_NAME
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.logz
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val reference: DatabaseReference
) : DatabaseInterface {

    override suspend fun insert(message: Message){
        val toUser = reference.child(message.uid!!).child(message.messageId!!)
        toUser.setValue(FirebaseMessage(
            uid = USER_ID,
            name = USER_NAME,
            message = message.message,
            status = null,
            timestamp = message.timestamp,
            messageId = message.messageId
        ))
        val fromUser = reference.child(USER_ID)
        fromUser.child(message.messageId!!).setValue(
            FirebaseMessage(
                uid = message.uid,
                name = message.name,
                status = MessageStatus.SENT,
                messageId = message.messageId
            )
        )
    }

    override suspend fun delete(message: Message) {
        (message as FirebaseMessage).apply {
            reference.child(USER_ID).child(this.messageId!!).removeValue()
        }
    }

    override suspend fun updateStatus(message: Message, status: MessageStatus) {
        val fb = FirebaseMessage(
                uid = message.uid,
                name = message.name,
                status = status,
                messageId = message.messageId
            )
        reference.child(message.uid!!)
            .child(message.messageId!!).setValue(fb)
    }

    override suspend fun getContacts():List<Contact>{
        val answer = reference.child(Constants.USERS_PATH).get().await()
        val contactsList = mutableListOf<Contact>()
        answer.children.forEach {
            it.getValue(Contact::class.java)?.let {contact->
                if(contact.uid != USER_ID)
                    contactsList += contact
            }
        }
        return contactsList
    }

    @ExperimentalCoroutinesApi
    fun addChatListener(): Flow<FirebaseMessage> =  callbackFlow{
        val chatListener = object : ChildEventListener{
            //Отфильтровываем только сообщения от пользователя ((from != null)).
            //Сообщения, отправленные пользователю отслеживаются в методе onChildChanged!
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.child("message").value?.let {
                 offer(snapshot.getValue(FirebaseMessage::class.java)!!)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.child("status").value?.let{
                    offer(
                        snapshot.getValue(FirebaseMessage::class.java)!!
                    )
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        }
        reference.child(Constants.USER_ID).addChildEventListener(chatListener)
        awaitClose {
            "await called".logz()
            reference.child(USER_ID).removeEventListener(chatListener)
        }
    }
}