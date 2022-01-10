package com.example.mymessenger.ui.fragments.privatechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.databinding.ActivityPrivateChatBinding
import com.example.mymessenger.room.Contact
import com.example.mymessenger.ui.fragments.chatlist.ChatsActivity
import com.example.mymessenger.utills.Constants.ACTION_PRIVATE_CHAT
import com.example.mymessenger.utills.Constants.CONTACT
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.PrivateChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PrivateChatActivity : AppCompatActivity() {
    private var _binding: ActivityPrivateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrivateChatViewModel by viewModels()
    private lateinit var _adapter: PrivateChatAdapter
    private lateinit var contact: Contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPrivateChatBinding.inflate(layoutInflater)
        setAdapter()
        setViewModel()
        setContentView(binding.root)
        binding.apply {
            messageL.isEndIconVisible = false
            messageEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(message: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(viewModel.isMessageValid(message) && !messageL.isEndIconVisible)
                        messageL.isEndIconVisible = true
                    else if(!viewModel.isMessageValid(message) && messageL.isEndIconVisible)
                        messageL.isEndIconVisible = false
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            messageL.setEndIconOnClickListener {
                binding.messageEt.apply {
                    viewModel.sendMessage(text.toString(), contact)
                    text?.clear()
                }
            }
        }
    }
    private fun setAdapter() {
        _adapter = PrivateChatAdapter()
        binding.chatRv.apply {
            (this.layoutManager as LinearLayoutManager).stackFromEnd = true
            adapter = _adapter
        }
    }
    private fun setViewModel() {
        viewModel.chat.onEach {
            _adapter.submitList(it)
            binding.chatRv.smoothScrollToPosition(_adapter.itemCount)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if(it.action == ACTION_PRIVATE_CHAT)
                contact = it.extras!!.getParcelable(CONTACT)!!
            "contact: $contact".logz()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Intent(this, ChatsActivity::class.java).run {
            startActivity(this)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}