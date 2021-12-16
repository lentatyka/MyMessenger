package com.example.mymessenger.di

import android.content.Context
import androidx.room.Room
import com.example.mymessenger.firebase.FirebaseAuthentication
import com.example.mymessenger.firebase.FirebaseRepository
import com.example.mymessenger.interfaces.Authenticator
import com.example.mymessenger.interfaces.DatabaseInterface
import com.example.mymessenger.room.ChatDAO
import com.example.mymessenger.room.ChatDatabase
import com.example.mymessenger.utills.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomdatabaseModule {
        @Singleton
        @Provides
        fun provideChatDatabase(
            @ApplicationContext app: Context
        ):ChatDatabase{
            return Room.databaseBuilder(
                app,
                ChatDatabase::class.java,
                Constants.SQLITE_BASE_NAME
            ).build()
        }

        @Provides
        @Singleton
        fun provideRoomDAO(db: ChatDatabase):ChatDAO{
            return db.getChatDAO()
        }
}