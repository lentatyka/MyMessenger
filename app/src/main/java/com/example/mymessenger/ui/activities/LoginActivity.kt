package com.example.mymessenger.ui.activities


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mymessenger.R
import com.example.mymessenger.databinding.ActivityLoginBinding
import com.example.mymessenger.utills.Constants.ACTION_SIGN_OUT
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(intent.action == ACTION_SIGN_OUT){
            val sharedPref = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE) ?: return
            sharedPref.edit().clear().apply()
            loginViewModel.signOut()
        }
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
    }



    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}