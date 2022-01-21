package com.example.mymessenger.firebase


import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.utills.Constants.USERS_PATH
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.MessageStatus
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.LoginViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val reference: DatabaseReference,
    private val storage: StorageReference
) : DatabaseInterface {


    @Synchronized override fun insertMessage(uid: String, message: Message) {
        reference.child(uid)
            .child(message.messageId)
            .setValue(
                message
            )
    }

    override suspend fun deleteMessage(message: Message) {
        (message as FirebaseMessage).apply {
            reference.child(USER_ID).child(this.messageId).removeValue()
        }
    }

    override suspend fun updateMessage(message: Message, status: MessageStatus) {
        val fb = FirebaseMessage(
            uid = message.uid,
            status = status,
            messageId = message.messageId,
            message = null,
            timestamp = null
        )
        reference.child(message.uid)
            .child(message.messageId).setValue(fb)
    }

    override suspend fun getContacts(filter: List<String>): List<FirebaseContact> {
        try{
            val contacts = reference.child(USERS_PATH).get().await()
            val contactsList = mutableListOf<FirebaseContact>()
            contacts.children.forEach { snapshot ->
                snapshot.getValue(FirebaseContact::class.java)?.let { contact ->
                    if (contact.uid != USER_ID && !filter.contains(contact.uid)){
                        contact.copy(
                            avatar = getFile(contact.uid)
                        ).also {
                            contactsList += it
                        }
                    }
                }
            }
            return contactsList
        }catch (e: FirebaseException){
            throw DatabaseException(LoginViewModel.ERROR_UNKNOWN)
        }
    }

    override suspend fun getContact(uid: String): FirebaseContact? {
        try{
            val answer = reference.child(USERS_PATH).child(uid).get().await()
            answer.getValue(FirebaseContact::class.java)?.let {
                return it.copy(
                    avatar = getFile(uid)
                )
            } ?: return null
        }catch (e: FirebaseException){
            throw DatabaseException(LoginViewModel.ERROR_UNKNOWN)
        }
    }

    override suspend fun insertFile() {
        TODO("Not yet implemented")
    }

    override suspend fun updateFile() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile() {
        TODO("Not yet implemented")
    }

    override suspend fun getFile(uid: String): ByteArray? {
        return try{
            storage.child(uid).getBytes(Long.MAX_VALUE).await()
        }catch (e: IOException){
            null
        }
    }



    fun addChatListener(callback: (Message) -> Unit) {
        reference.child(USER_ID).addChildEventListener(object : ChildEventListener {
            //Отфильтровываем только сообщения от пользователя ((from != null)).
            //Сообщения, отправленные пользователю отслеживаются в методе onChildChanged!
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                callback(snapshot.getValue(FirebaseMessage::class.java)!!)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                callback(
                        snapshot.getValue(FirebaseMessage::class.java)!!
                )
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}