package com.example.mymessenger.di

import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.firebase.FirebaseAuthentication
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.Authenticator
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class FirebaseModel {

    @Binds
    abstract fun provideFirebaseRepository(repo: FirebaseRepository): DatabaseInterface

    @Binds
    abstract fun bindAuthenticator(auth: FirebaseAuthentication): Authenticator<Unit>

    companion object {
        @Provides
        fun provideFirebaseReference(): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
        }
    }
}