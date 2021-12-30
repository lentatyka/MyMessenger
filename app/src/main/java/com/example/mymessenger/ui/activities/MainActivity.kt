package com.example.mymessenger.ui.activities


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mymessenger.MyService
import com.example.mymessenger.NavGraphDirections
import com.example.mymessenger.R
import com.example.mymessenger.databinding.ActivityMainBinding
import com.example.mymessenger.utills.Constants.ACTION_START_SERVICE
import com.example.mymessenger.utills.Constants.ACTION_STOP_SERVICE
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.logz
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mService: MyService
    private var mBound = false
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var onDestinationListener: NavController.OnDestinationChangedListener

    lateinit var connection: ServiceConnection

    companion object {
        private var destination_: Int? = null
        val destination: Int? get() = destination_
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MyService.LocalBinder
                mService = binder.getService()
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                destination_ = null
                mBound = false
            }
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        //Проверяем, был ли старт черех push-уведомление
        navigateToPrivateChatFragment(intent)
        onDestinationListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                destination_ = destination.id
            }
        navController.addOnDestinationChangedListener(
            onDestinationListener
        )
    }

    override fun onStart() {
        super.onStart()
        startMyService()
            Intent(this, MyService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                mBound = true
            }
    }

    private fun stopMyService() {
        if(MyService.isRunning){
            Intent(this, MyService::class.java).also {intent ->
                intent.action = ACTION_STOP_SERVICE
                stopService(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        destination_ = null
        if (mBound) {
            unbindService(connection)
        }
        navController.removeOnDestinationChangedListener(onDestinationListener)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent?) {
        "ACTION: ${intent?.action}".logz()
        super.onNewIntent(intent)
        //Если было нажато push-уведомление, переходим в приватный чат
        navigateToPrivateChatFragment(intent)
    }

    private fun navigateToPrivateChatFragment(intent: Intent?) {
        if (intent?.action == "TEST") {
            intent.extras!!.getParcelable<Contact>("contacts").also {
                val action = NavGraphDirections.actionGlobalToPrivateChatFragment(it!!)
                navController.navigate(action)
            }
        }
    }

    private fun startMyService() {
        if (!MyService.isRunning) {
            Intent(this, MyService::class.java).also {
                it.action = ACTION_START_SERVICE
                startService(it)
            }
        }
    }
}