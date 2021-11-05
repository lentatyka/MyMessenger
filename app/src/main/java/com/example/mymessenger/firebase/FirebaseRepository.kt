package com.example.mymessenger.firebase


import android.util.Log
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.room.Chat
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.Contact
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val reference: DatabaseReference
) : DatabaseInterface {

    override suspend fun insert(message: Message) {
        val map = hashMapOf<String, Any>()
        val reff = reference.child("DS")

        val key = reff.push().key.toString()
        map["what"] = "test"
        reff.child(key).updateChildren(map)
    }

    override suspend fun delete(message: Message) {
    }

    override fun getContacts(callback:(List<Contact?>)->Unit) {
        val contactsList = mutableListOf<Contact?>()
        reference.child(Constants.USERS_PATH).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        snapshot.children.forEach {
                            val contact = it.getValue(Contact::class.java)
                            contactsList += contact
                        }
                        callback(contactsList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}