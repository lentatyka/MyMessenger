package com.example.mymessenger.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.adapters.ContactsAdapter
import com.example.mymessenger.databinding.FragmentContactsBinding
import com.example.mymessenger.utills.State
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    lateinit var _adapter: ContactsAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModel()
    }


    private fun setViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.getContacts().onEach { state->
                when(state){
                    is State.Contacts ->{
                        _adapter.setList(state.contacts.toList())
                        binding.loader.visibility = View.GONE
                        binding.contactsRv.visibility = View.VISIBLE
                    }
                    is State.Waiting->{
                        binding.loader.visibility = View.VISIBLE
                        binding.contactsRv.visibility = View.GONE
                    }
                    is State.Error->{
                        //Temp for testing
                        Toast.makeText(context, "Error: ${state.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else->{
                        //nothing
                    }
                }
            }.collect()
        }
    }

    private fun setAdapter() {
        _adapter = ContactsAdapter{userId->
            val action = ContactsFragmentDirections.actionContactsFragmentToPrivateChatFragment(userId)
            findNavController().navigate(action)
        }
        binding.contactsRv.apply {
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            adapter = _adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}