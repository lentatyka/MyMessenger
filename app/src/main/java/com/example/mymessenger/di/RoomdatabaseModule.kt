package com.example.mymessenger.di

import android.content.Context
import androidx.room.Room
import com.example.mymessenger.room.RoomDao
import com.example.mymessenger.room.RoomDatabase
import com.example.mymessenger.utills.Constants
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
        ):RoomDatabase{
            return Room.databaseBuilder(
                app,
                RoomDatabase::class.java,
                Constants.SQLITE_BASE_NAME
            ).build()
        }

        @Provides
        @Singleton
        fun provideRoomDAO(db: RoomDatabase):RoomDao{
            return db.getChatDAO()
        }
}