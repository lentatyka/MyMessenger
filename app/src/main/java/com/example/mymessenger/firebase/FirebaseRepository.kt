package com.example.mymessenger.firebase

import android.util.Log
import com.example.mymessenger.utills.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val reference: DatabaseReference
):DatabaseInterface{

    override suspend fun insert(message: Message) {
//    val map = hashMapOf<String, Any>()
//        val reff=  reference.child(auth.currentUser?.uid.toString())
//
//        val key = reff.push().key.toString()
//        map["what"] = "test"
//        reff.child(key).updateChildren(map)
    }

    override suspend fun delete(message: Message) {

    }
    fun login(onSuccess: ()->Unit, onFail: (String)->Unit){
        auth.signInWithEmailAndPassword(Constants.EMAIL, Constants.PASSWORD)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFail(it.message.toString())
            }
    }
    fun signOut(){
        auth.signOut()
    }
}