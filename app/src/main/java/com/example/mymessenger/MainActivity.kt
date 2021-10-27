package com.example.mymessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.mymessenger.databinding.ActivityMainBinding
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.isValidEmail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAuth.setOnClickListener {
            if(checkFields()){
                Constants.EMAIL = binding.etEmail.text.toString()
                Constants.PASSWORD = binding.etPassword.text.toString()
                viewModel.initFB({showToast(null)}){showToast(it)}
            }
            else
                showToast("Bad fields data")
        }
    }

    private fun checkFields(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        return email.isValidEmail() && password.isNotEmpty()
    }

    private fun showToast(text: String?){
        Toast.makeText(this, text ?: "Success!", Toast.LENGTH_SHORT ).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}