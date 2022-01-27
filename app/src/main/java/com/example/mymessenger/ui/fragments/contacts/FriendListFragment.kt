package com.example.mymessenger.ui.fragments.contacts

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentFriendlistBinding
import com.example.mymessenger.room.RoomContact
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.ui.fragments.chatlist.ViewType
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.viewmodels.MainViewModel
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FriendListFragment : Fragment() {
    private var _binding: FragmentFriendlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var _adapter: ContactsAdapter
    private lateinit var _menu: Menu
    private var contacts = mutableListOf<RoomContact>()
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendlistBinding.inflate(layoutInflater, container, false)
        setToolbar()
        return binding.root
    }

    private fun setToolbar() {
        (activity as MainActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            val view = it.customView
            view.findViewById<ShapeableImageView>(R.id.avatar_iv).visibility = View.GONE
            view.findViewById<TextView>(R.id.title_tv).text = getString(R.string.friends)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModel()
        binding.btnFindContact.setOnClickListener {
            findNavController().navigate(
                FriendListFragmentDirections.actionContactsFragmentToContactsFragment2()
            )
        }
    }

    private fun setViewModel() {
        viewModel.getFriendList().onEach { state->
                _adapter.setList(state)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun setAdapter() {
        _adapter = ContactsAdapter{contact, viewtype, isChanged->
            when(viewtype){
                ViewType.CHAT ->{
                    findNavController().navigate(
                        FriendListFragmentDirections.actionContactsFragmentToPrivateChatFragment(
                            contact
                        )
                    )
                }
                ViewType.REMOVE_CONTACT ->{
                    contact as RoomContact
                    if(isChanged){
                        contacts += contact
                    }else
                        contacts -= contact
                    _menu.findItem(R.id.item_delete).isVisible = contacts.isNotEmpty()
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
                    FriendListFragmentDirections.actionContactsFragmentToChatListFragment()
                )
            }
            R.id.item_delete ->{
                viewModel.deleteContacts(contacts)
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}