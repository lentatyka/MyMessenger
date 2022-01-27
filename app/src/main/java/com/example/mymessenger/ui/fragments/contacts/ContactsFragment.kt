package com.example.mymessenger.ui.fragments.contacts

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentContactsBinding
import com.example.mymessenger.interfaces.Contact
import com.example.mymessenger.ui.fragments.chatlist.ViewType
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.showToast
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var _adapter: ContactsAdapter
    private lateinit var _menu: Menu
    private var contacts = mutableListOf<Contact>()
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_contacts, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModel()
    }

    private fun setViewModel() {
        viewModel.findContacts().onEach { state->
           binding.isVisible = state
            when(state){
                is State.Object ->{
                    _adapter.setList(state.contacts.toList())
                }
                is State.Error->{
                    getString(R.string.error_unknown).showToast(requireContext())
                }
                else->{
                    //nothing
                }
            }
        }.launchWhenStarted(lifecycleScope)
    }
    private fun setAdapter() {
        _adapter = ContactsAdapter{contact, viewtype, isChanged->
            when(viewtype){
                ViewType.CHAT -> {
                    findNavController().navigate(
                        ContactsFragmentDirections.actionContactsFragment2ToPrivateChatFragment(
                            contact
                        )
                    )
                }
                ViewType.ADD_CONTACT ->{
                    if(isChanged){
                        contacts += contact
                    }else
                        contacts -= contact
                    _menu.findItem(R.id.item_add).isVisible = contacts.isNotEmpty()
                }
            }
        }
        binding.contactsRv.apply {
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            adapter = _adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contactmenu, menu)
        _menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                findNavController().navigate(
                    ContactsFragmentDirections.actionContactsFragment2ToContactsFragment()
                )
            }
            R.id.item_add ->{
                    viewModel.addContactToFriend(contacts)
                    findNavController().navigate(
                        ContactsFragmentDirections.actionContactsFragment2ToContactsFragment()
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