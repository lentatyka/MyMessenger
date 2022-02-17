package com.example.mymessenger.services

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.repository.Repository
import com.example.mymessenger.room.RoomContact
import com.example.mymessenger.utills.*
import com.example.mymessenger.utills.Constants.ACTION_START_SERVICE
import com.example.mymessenger.utills.Constants.ACTION_STOP_SERVICE
import com.example.mymessenger.utills.Constants.NEW_MESSAGE
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DatabaseService : LifecycleService() {

    //Not implemented part network connection status. Coming soon...
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager

    private val binder = LocalBinder()
    private var isBinded = false

    private var _isRunning: Boolean = false
    val isRunning: Boolean get() = _isRunning

    private var _isConnection: Boolean = false
    val isConnection: Boolean get() = _isConnection

    private val _message = MutableStateFlow<Contact?>(null)
    val message: StateFlow<Contact?> = _message.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): DatabaseService = this@DatabaseService
    }

    @Inject
    lateinit var repository: Repository



    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        isBinded = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBinded = false
        _message.value = null
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_START_SERVICE -> {
                    networkCallback = object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            registerConnectiveManager()
                        }

                        override fun onLost(network: Network) {
                            unregisterConnectiveManager()
                        }
                    }
                    connectivityManager = applicationContext
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    connectivityManager.also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            it.registerDefaultNetworkCallback(networkCallback)
                        }else{
                            val request = NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .build()
                            it.registerNetworkCallback(request, networkCallback)
                        }
                    }
                    registerConnectiveManager()

                    _isRunning = true
                    return START_STICKY
                }
                ACTION_STOP_SERVICE -> {
                    stopForeground(true)
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        _isRunning = false
        stopSelf()
    }

    private fun startChatListener() {
        fun checkUndeliveredMessages(){
            CoroutineScope(Dispatchers.IO).launch {
                repository.checkUndeliveredMessages()
            }
        }
        repository.getChatListenerRemote { message ->
            if (message.message != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    var contact = repository.checkContactLocal(message.uid)
                    if (contact == null) {
                        contact = getContact(message.uid)
                        startService(
                            contact.copy(
                                info = message.message
                            )
                        )
                    }
                    repository.receiveMessage(message)
                    if (isBinded)
                        _message.value = contact.copy(
                            info = message.message
                        )
                }
            } else {
                //Receive message status
                CoroutineScope(Dispatchers.IO).launch {
                    repository.receiveMessageStatus(message)
                }
            }
        }
    }

    private fun stopChatListener(){
        repository.removeChatListenerRemote()
    }

    private fun startService(contact: Contact) {
        Intent(this@DatabaseService, NotificationService::class.java).apply {
            action = ACTION_START_SERVICE
            putExtra(NEW_MESSAGE, contact)
            startService(this)
        }
    }

    private suspend fun getContact(uid: String): RoomContact {
        //New contact. Get data from remote database
        repository.getContactRemote(listOf(uid), true).first().also {
            val contact = RoomContact(
                uid = it.uid,
                nickname = it.nickname,
                email = it.email,
                avatar = it.avatar,
                info = null,
                isOwn = false,
            )
            repository.insertContactLocal(
                listOf(contact)
            )
            return contact
        }
    }

    private fun registerConnectiveManager(){
        _isConnection = true
        startChatListener()

    }

    private fun unregisterConnectiveManager(){
        _isConnection = false
        stopChatListener()

    }

    override fun onDestroy() {
        _isRunning = false
        _isConnection = false
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }
}