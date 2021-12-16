package com.example.mymessenger.firebase


import android.util.Log
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.room.RoomMessage
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.getCurrentTime
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

    override suspend fun insert(message: Message):String {
        val toUser = reference.child(message.uid!!).push()
        val key = toUser.key.toString()
        toUser.setValue(FirebaseMessage(
            uid = USER_ID,
            name = message.name,
            from = message.to,
            to = null,
            status = null,
            timestamp = message.timestamp,
            messageId = key
        ))
        val fromUser = reference.child(USER_ID)
        fromUser.child(key).setValue(
            FirebaseMessage(
                uid = message.uid,
                name = message.name,
                status = MessageStatus.SENT,
                messageId = key
            )
        )
        return key
    }

    override suspend fun delete(message: Message) {
        (message as FirebaseMessage).apply {
            reference.child(USER_ID).child(this.messageId.toString()).removeValue()
            updateStatus(this)
        }
    }

    override suspend fun updateStatus(message: Message) {
//        (message as FirebaseMessage).apply {
//            reference.child(this.uid!!).child(this.messageId).child("status")
//        }
    }

    suspend fun getContacts():List<Contact?>{
        val answer = reference.child(Constants.USERS_PATH).get().await()
        val contactsList = mutableListOf<Contact?>()
        answer.children.forEach {
            contactsList += it.getValue(Contact::class.java)
        }
        return contactsList
    }

    @ExperimentalCoroutinesApi
    fun addChatListener(): Flow<FirebaseMessage> =  callbackFlow{
        val chatListener = object : ChildEventListener{
            //Отфильтровываем только сообщения от пользователя ((from != null)).
            //Сообщения, отправленные пользователю отслеживаются в методе onChildChanged!
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.child("from").apply{
                    offer(
                        snapshot.getValue(FirebaseMessage::class.java)!!
                    )
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                /*Статус о прочитанном сообщении отслеживаем тут! Пример как WA:
                одна галочка - сообщение отправлено на сервер
                две серые галочки - доставлено до абонента
                две синие галочки - прочитано!
                В моем примере вместо галочек самолетики :D
                */
                snapshot.child("to").apply{
                    offer(
                        snapshot.getValue(FirebaseMessage::class.java)!!
                    )
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("TAG", "CHILD Removed-> ${snapshot.value}")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TAG", "CHILD Moved-> ${snapshot.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "CHILD ERROR-> ${error.code}")
            }
        }
        reference.child(Constants.USER_ID).addChildEventListener(chatListener)
        awaitClose {
            Log.d("TAG", "AWAIT CALLED")
            reference.child(Constants.USER_ID).removeEventListener(chatListener)
        }
    }
}