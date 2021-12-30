package com.example.mymessenger.ui.fragments.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.databinding.FragmentContactsBinding
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    lateinit var _adapter: ContactsAdapter
    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModel()
    }


    @ExperimentalCoroutinesApi
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
            val action = ContactsFragmentDirections.actionContactsFragmentToPrivateChatFragment(
                contact
            )
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