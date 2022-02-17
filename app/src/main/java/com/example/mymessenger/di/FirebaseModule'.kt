package com.example.mymessenger.di

import com.example.mymessenger.firebase.FirebaseAuthentication
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.interfaces.DatabaseInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract  class FirebaseModule {
    @Binds
    abstract fun provideFirebaseRepository(repo: FirebaseRepository): DatabaseInterface

    @Binds
    abstract fun bindAuthenticator(auth: FirebaseAuthentication): Authenticator<@JvmSuppressWildcards FirebaseUser?>

    companion object{

        @Provides
        @Singleton
        fun provideFirebaseReference(): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
        }

        @Provides
        @Singleton
        fun provideFirebaseStorage(): StorageReference {
            return Firebase.storage.reference
        }

        @Provides
        @Singleton
        fun provideFirebaseAuthentication():FirebaseAuth{
            return FirebaseAuth.getInstance()
        }
    }
}