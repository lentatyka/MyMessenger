package com.example.mymessenger.ui.fragments.chatlist

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentChatlistBinding
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.ui.activities.LoginActivity
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.Constants.ACTION_SIGN_OUT
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
            view.findViewById<ShapeableImageView>(R.id.avatar_iv).visibility = View.GONE
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
                        viewModel.addContactToFriend(listOf(contact))
                    }
                }
            }
        }
        binding.chatsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (chatlistAdapter)
        }
    }

    private fun showAlertDialog(contact: Contact?, message: String, callback:()->Unit){
        val icon = contact?.let {
            BitmapDrawable(
                resources,
                BitmapFactory.decodeByteArray(it.avatar, 0, it.avatar!!.size))
        }
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(icon)
            .setTitle(contact?.nickname)
            .setMessage(message)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.accept)){_, _->
                callback()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chatlistmenu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_sign_out ->{
                showAlertDialog(null, getString(R.string.sign_out)){
                    Intent(context, LoginActivity::class.java).also {
                        it.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        it.action = ACTION_SIGN_OUT
                        startActivity(it)
                    }
                }
            }
            R.id.item_settings->{
                findNavController().navigate(
                    ChatListFragmentDirections.actionChatListFragmentToSettingsFragment()

                )
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}