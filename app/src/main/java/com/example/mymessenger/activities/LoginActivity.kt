package com.example.mymessenger.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mymessenger.activities.main.MainActivity
import com.example.mymessenger.databinding.ActivityLoginBinding
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.State
import com.example.mymessenger.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Ignore login then delete
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent, null)
        binding.btnAuth.setOnClickListener {
            if(checkFields()){
                Constants.EMAIL = binding.etEmail.text.toString()
                Constants.PASSWORD = binding.etPassword.text.toString()
                lifecycleScope.launchWhenStarted {
                    loginViewModel.signIn().onEach {state->
                        when(state){
                            is State.Success ->{
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent, null)
                            }
                            is State.Error ->{
                                showToast(state.exception.toString())
                            }
                            else->{
                                //nothing
                            }
                        }
                    }.collect()
                }
            }
            else
                showToast("Bad fields data")
        }
    }

    private fun checkFields(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        return loginViewModel.isDataValid(email, password)
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT ).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}