package com.example.mymessenger.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.R
import com.example.mymessenger.ui.activities.LoginActivity
import com.example.mymessenger.utills.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    fun provideSoundNotificationUri(): Uri? {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        soundUri: Uri?
    ): NotificationCompat.Builder{
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setOngoing(false)
            .setSound(soundUri)
            .setSmallIcon(R.drawable.ic_notification)
    }
}