package com.example.mymessenger.ui.fragments.chatlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.databinding.ActivityChatsBinding
import com.example.mymessenger.ui.fragments.privatechat.PrivateChatActivity
import com.example.mymessenger.utills.Constants.ACTION_PRIVATE_CHAT
import com.example.mymessenger.utills.Constants.CONTACT_EXTRA
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.viewmodels.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ChatsActivity : AppCompatActivity() {
    private var _binding: ActivityChatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatListViewModel by viewModels()
    @Inject
    lateinit var filePath: File
    private lateinit var chatlistAdapter: ChatListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatsBinding.inflate(layoutInflater)
        setAdapter()
        setViewModel()
        setContentView(binding.root)
    }

    private fun setAdapter() {
        chatlistAdapter = ChatListAdapter(
            filePath
        ){contact->
            Intent(this, PrivateChatActivity::class.java).also {
                it.action = ACTION_PRIVATE_CHAT
                it.putExtra(CONTACT_EXTRA, contact)
                startActivity(it)
            }
        }
        binding.chatsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (chatlistAdapter)
        }
    }
    private fun setViewModel() {
        viewModel.getChatList().onEach { message->
            chatlistAdapter.submitList(message)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}