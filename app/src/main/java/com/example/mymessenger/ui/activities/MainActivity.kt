package com.example.mymessenger.ui.activities


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mymessenger.services.DatabaseService
import com.example.mymessenger.NavGraphDirections
import com.example.mymessenger.R
import com.example.mymessenger.databinding.ActivityMainBinding
import com.example.mymessenger.services.NotificationService
import com.example.mymessenger.utills.Constants.ACTION_START_SERVICE
import com.example.mymessenger.utills.Constants.ACTION_STOP_SERVICE
import com.example.mymessenger.utills.Constants.CONTACT
import com.example.mymessenger.utills.Constants.CONTACT_ID
import com.example.mymessenger.utills.Constants.NEW_MESSAGE
import com.example.mymessenger.utills.Constants.START_PRIVATE_CHAT
import com.example.mymessenger.room.Contact
import com.example.mymessenger.ui.fragments.contacts.ContactsFragmentDirections
import com.example.mymessenger.ui.fragments.privatechat.PrivateChatFragmentDirections
import com.example.mymessenger.utills.launchWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mService: DatabaseService
    private var mBound = false
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    lateinit var connection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as DatabaseService.LocalBinder
                mService = binder.getService()
                startDatabaseService()
                startServiceListener()
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mBound = false
            }
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        //setupActionBarWithNavController(navController)
        //Проверяем, был ли старт черех push-уведомление
        navigateToPrivateChatFragment(intent)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, DatabaseService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            mBound = true
        }
    }

    private fun stopDatabaseService() {
        if (mService.isRunning) {
            Intent(this, DatabaseService::class.java).also { intent ->
                intent.action = ACTION_STOP_SERVICE
                stopService(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(connection)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //Если было нажато push-уведомление, переходим в приватный чат
        navigateToPrivateChatFragment(intent)
    }

    private fun navigateToPrivateChatFragment(intent: Intent?) {
        if (intent?.action == START_PRIVATE_CHAT) {
            intent.extras?.getParcelable<Contact>(CONTACT).also {
                val action = NavGraphDirections.actionGlobalToPrivateChatFragment(it!!)
                navController.navigate(action)
            }
        }
    }

    private fun startDatabaseService() {
        if (!mService.isRunning) {
            Intent(this, DatabaseService::class.java).also {
                it.action = ACTION_START_SERVICE
                startService(it)
            }
        }
    }

    private fun startServiceListener() {
        mService.message.onEach { contact ->
            contact?.let {
                when (navController.currentDestination!!.id) {
                    R.id.privateChatFragment -> {
                        CONTACT_ID?.let {id->
                            if (contact.uid != id)
                                startNotification(contact)
                        }
                    }
                    R.id.contactsFragment ->
                        startNotification(contact)
                    else -> {
                        //nothing
                    }
                }
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun startNotification(contact: Contact) {
        Intent(this, NotificationService::class.java).apply {
            action = ACTION_START_SERVICE
            putExtra(NEW_MESSAGE, contact)
            startService(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.currentDestination?.let {
            when(it.id){
                R.id.privateChatFragment ->
                    navController.navigate(
                        PrivateChatFragmentDirections.actionPrivateChatFragmentToChatListFragment()
                    )
                R.id.contactsFragment ->
                    navController.navigate(
                        ContactsFragmentDirections.actionContactsFragmentToChatListFragment()
                    )
                R.id.chatListFragment ->
                    this.finish()
            }
        }
    }
}