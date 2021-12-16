package com.example.mymessenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mymessenger.databinding.ActivityMainBinding
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var mService: MyService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.LocalBinder
            mService = binder.getService()
            _mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _mBound = false
        }
    }

    companion object {
        private var _mBound: Boolean = false
        val mBound: Boolean get() = _mBound
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        //Проверяем, был ли старт черех push-уведомление
        navigateToPrivateChatFragment(intent)
//        startObserveService()
    }

//    private fun startObserveService() {
//        MyService.messages.observe(this) { message ->
//            //viewmodel отработать message
//            when (navController.currentDestination!!.id) {
//                R.id.chatListFragment ->{
//                    viewModel.updateLastMessage()
//                }
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        Intent(this, MyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        _mBound = false
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
            Log.d("TAG", "navToPrivinner")
            navController.navigate(R.id.action_global_to_PrivateChatFragment)
        }
    }
}