package com.example.mymessenger.ui.fragments.chatlist

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentChatlistBinding
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
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
        (activity as MainActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            val view = it.customView
            view.findViewById<CardView>(R.id.avatar_card).visibility = View.GONE
            view.findViewById<TextView>(R.id.title_tv).text = getString(R.string.app_name)
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
        viewModel.getChatList().onEach { message ->
            "test: $message".logz()
            chatlistAdapter.submitList(message)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun setAdapter() {
        chatlistAdapter = ChatListAdapter { contact, viewType ->
            when (viewType) {
                ViewType.CHAT -> {
                    val action =
                        ChatListFragmentDirections.actionChatListFragmentToPrivateChatFragment(
                            contact
                        )
                    findNavController().navigate(action)
                }
                ViewType.DELETE_CHAT ->{
                    showAlertDialog(contact, resources.getString(R.string.chat_delete)){
                        viewModel.deleteChat(contact.uid!!)
                    }
                }
                ViewType.ADD_CONTACT ->{
                    showAlertDialog(contact, getString(R.string.contact_add)){
                        viewModel.addContactToFriend(contact)
                    }
                }
            }
        }
        binding.chatsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (chatlistAdapter)
        }
    }

    private fun showAlertDialog(contact: Contact, message: String, callback:()->Unit){
        val icon = BitmapDrawable(
            resources,
            BitmapFactory.decodeByteArray(contact.avatar, 0, contact.avatar!!.size))
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(icon)
            .setTitle(contact.nickname)
            .setMessage(message)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.accept)){_, _->
                callback()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}