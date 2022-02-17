package com.example.mymessenger.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.mymessenger.R
import com.example.mymessenger.firebase.FirebaseContact
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.Constants.ACTION_START_SERVICE
import com.example.mymessenger.utills.Constants.ACTION_STOP_SERVICE
import com.example.mymessenger.utills.Constants.CHANNEL_SIREN_DESCRIPTION
import com.example.mymessenger.utills.Constants.CONTACT
import com.example.mymessenger.utills.Constants.NEW_MESSAGE
import com.example.mymessenger.utills.Constants.NOTIFICATION_CHANNEL_ID
import com.example.mymessenger.utills.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.mymessenger.utills.Constants.START_PRIVATE_CHAT
import com.example.mymessenger.utills.logz
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    private lateinit var currentNotification: NotificationCompat.Builder
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        currentNotification = baseNotificationBuilder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_SERVICE -> {
                    intent.extras!!.getParcelable<Contact>(NEW_MESSAGE).also {
                        startForegroundService(it!!)
                    }
                }
                ACTION_STOP_SERVICE -> {
                    stopForeground(true)
                    killService()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun killService() {
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService(contact: Contact) {
        val sIntent = Intent(this, MainActivity::class.java).apply {
            action = START_PRIVATE_CHAT
            putExtra(
                CONTACT,
                contact
            )
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            sIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notifyManager)
        }
        currentNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotification, ArrayList<NotificationCompat.Action>())
        }
        val image = if(contact.avatar != null){
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            BitmapFactory.decodeByteArray(contact.avatar, 0, contact.avatar!!.size, options)
        }else{
           ResourcesCompat.getDrawable(resources, R.drawable.ic_avatar, theme)!!.toBitmap()
        }

        currentNotification
            .setContentText(contact.info)
            .setContentTitle(contact.nickname)
            .setContentIntent(pendingIntent)
            .setLargeIcon(createRoundedIcon(image))
        notifyManager.notify(100500, currentNotification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = Color.GRAY
            enableLights(true)
            enableVibration(true)
            description = CHANNEL_SIREN_DESCRIPTION
        }
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION),
            audioAttributes
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        "DEST".logz()
        super.onDestroy()
    }

    private fun createRoundedIcon(bitmap: Bitmap):Bitmap{
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawCircle(
                (bitmap.width / 2).toFloat(),
                (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(),
                paint
            )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
    }
}