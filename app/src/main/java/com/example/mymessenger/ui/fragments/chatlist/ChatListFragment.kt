package com.example.mymessenger.ui.fragments.chatlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentChatlistBinding
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.viewmodels.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatListViewModel by viewModels()
    @Inject
    lateinit var filePath: File
    private lateinit var chatlistAdapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatlistBinding.inflate(layoutInflater, container, false)
        setToolbar()
        return binding.root
    }

    private fun setToolbar() {
        (activity as MainActivity).app_toolbar?.let {
            it.back_iv.visibility = View.GONE
            it.card_view.visibility = View.GONE
            it.title_tv.text = getString(R.string.chats)
        }
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
        viewModel.getChatList().onEach { message->
            chatlistAdapter.submitList(message)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun setAdapter() {
        chatlistAdapter = ChatListAdapter(
            filePath
        ){contact->
            val action = ChatListFragmentDirections.actionChatListFragmentToPrivateChatFragment(contact)
            findNavController().navigate(action)
        }
        binding.chatsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (chatlistAdapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}