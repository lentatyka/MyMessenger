package com.example.mymessenger.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.mymessenger.R
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.utills.Constants.USER_NAME
import com.example.mymessenger.utills.logz

class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref =
            getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE) ?: return
        sharedPref.apply {
            if(contains(getString(R.string.user_name)) && contains(getString(R.string.user_id))){
                getString(getString(R.string.user_name), null)?.let {
                    USER_NAME = it
                }
                getString(getString(R.string.user_id), null)?.let {
                    USER_ID = it
                }
                launchActivity(MainActivity::class.java)
            }else{
                launchActivity(LoginActivity::class.java)
            }
        }
    }

    private fun <T: Activity> launchActivity(activity: Class<T>) {
        Intent(this, activity).also { intent->
//            intent.addFlags(
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//            )
            startActivity(intent)
            finish()
        }
    }
}