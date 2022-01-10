package com.example.mymessenger.ui.fragments.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.databinding.ActivityContactsBinding
import com.example.mymessenger.ui.fragments.chatlist.ChatsActivity
import com.example.mymessenger.ui.fragments.privatechat.PrivateChatActivity
import com.example.mymessenger.utills.Constants.ACTION_PRIVATE_CHAT
import com.example.mymessenger.utills.Constants.CONTACT
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.viewmodels.ContactsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ContactsActivity : AppCompatActivity() {
    private var _binding: ActivityContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var _adapter: ContactsAdapter
    private val viewModel: ContactsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAdapter()
        setViewModel()
        setContentView(binding.root)
    }
    private fun setViewModel() {
        viewModel.getContacts().onEach { state->
            when(state){
                is State.Object ->{
                    _adapter.setList(state.contacts.toList())
                    binding.loader.visibility = View.GONE
                    binding.contactsRv.visibility = View.VISIBLE
                }
                is State.Loading->{
                    binding.loader.visibility = View.VISIBLE
                    binding.contactsRv.visibility = View.GONE
                }
                is State.Error->{
                    //Temp for testing
//
                }
                else->{
                    //nothing
                }
            }
        }.launchWhenStarted(lifecycleScope)

    }
    private fun setAdapter() {
        _adapter = ContactsAdapter{contact->
            Intent(this, PrivateChatActivity::class.java).also {
                it.action = ACTION_PRIVATE_CHAT
                it.putExtra(CONTACT, contact)
                startActivity(it)
            }
        }
        binding.contactsRv.apply {
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            adapter = _adapter
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Intent(this, ChatsActivity::class.java).run{
            startActivity(this)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}