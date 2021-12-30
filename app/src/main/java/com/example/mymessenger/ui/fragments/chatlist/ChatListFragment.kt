package com.example.mymessenger.ui.fragments.chatlist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.MyService
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentChatlistBinding
import com.example.mymessenger.utills.Constants.ACTION_START_SERVICE
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.messageToContact
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var chatlistAdapter: ChatListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatlistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddContact.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_contactsFragment)
        }
        setAdapter()
        setViewModel()
    }

    private fun setViewModel() {
        viewModel.getChatList()
        viewModel.getChatList().onEach { message->
            chatlistAdapter.submitList(message)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun setAdapter() {
        chatlistAdapter = ChatListAdapter(){message->
            val contact = message.messageToContact()
            val action = ChatListFragmentDirections.actionChatListFragmentToPrivateChatFragment(contact)
            findNavController().navigate(action)
        }
        binding.chatlistRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (chatlistAdapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}