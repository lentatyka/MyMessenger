package com.example.mymessenger.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModel {

    @Provides
    @Singleton
    fun provideFirebaseAuth():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseReference():DatabaseReference{
        return FirebaseDatabase.getInstance().reference
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(auth: FirebaseAuth, ref: DatabaseReference):DatabaseInterface{
        return FirebaseRepository(auth, ref)
    }
}