package com.example.mymessenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mymessenger.databinding.ActivityMainBinding
import com.example.mymessenger.fragments.login.LoginViewModel
import com.example.mymessenger.utills.Contact
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mService: MyService
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var onDestinationListener: NavController.OnDestinationChangedListener

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.LocalBinder
            mService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            destination_ = null
        }
    }

    companion object {
        private var destination_:Int? = null
        val destination: Int? get() = destination_
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        checkSharedPreferences()
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

    private fun checkSharedPreferences() {
        val sharedPref = getSharedPreferences(getString(R.string.auth), Context.MODE_PRIVATE) ?: return
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        destination_ = null
        unbindService(connection)
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
}