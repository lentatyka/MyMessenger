package com.example.mymessenger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mymessenger.firebase.FirebaseRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(app:Application, private val fb: FirebaseRepository):AndroidViewModel(app) {



    fun initFB(onSuccess: ()->Unit, onFail: (String)->Unit){
        fb.login({onSuccess()}){onFail(it)}
    }



}