package com.example.mymessenger.firebase


import android.util.Log
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.State
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val reference: DatabaseReference
) : DatabaseInterface {
    lateinit var chatListener: ChildEventListener

    override suspend fun insert(message: MessageFB) {
        val map = hashMapOf<String, Any>()
        val reff = reference.child("DS")

        val key = reff.push().key.toString()
        map["what"] = "test"
        reff.child(key).updateChildren(map)
    }

    override suspend fun delete(message: MessageFB) {
    }



    suspend fun getContacts():List<Contact?>{
        val answer = reference.child(Constants.USERS_PATH).get().await()
        val contactsList = mutableListOf<Contact?>()
        answer.children.forEach {
            contactsList += it.getValue(Contact::class.java)
        }
        return contactsList
    }

    fun addChatListener(){
        chatListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TAG", "CHILD ADDED-> ${snapshot.value}")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("TAG", "CHILD Changed-> ${snapshot.value}")
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

        reference.child("tree").addChildEventListener(chatListener)
    }
    fun removeChatListener(){
        reference.child("tree").removeEventListener(chatListener)
    }
}