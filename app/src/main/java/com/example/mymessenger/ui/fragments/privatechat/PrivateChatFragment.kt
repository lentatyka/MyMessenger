package com.example.mymessenger.ui.fragments.privatechat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentPrivateChatBinding
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.Constants.CONTACT_ID
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.PrivateChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_private_chat.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PrivateChatFragment : Fragment() {
    private val args: PrivateChatFragmentArgs by navArgs()
    private var _binding: FragmentPrivateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrivateChatViewModel by viewModels()
    private lateinit var _adapter: PrivateChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivateChatBinding.inflate(layoutInflater, container, false)
        setToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModel()
        binding.apply {
            messageL.isEndIconVisible = false
            messageEt.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(message: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(viewModel.isMessageValid(message) && !messageL.isEndIconVisible)
                        messageL.isEndIconVisible = true
                    else if(!viewModel.isMessageValid(message) && messageL.isEndIconVisible)
                        messageL.isEndIconVisible = false
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            messageL.setEndIconOnClickListener {
                binding.messageEt.apply {
                    viewModel.sendMessage(text.toString(), args.contact)
                    text?.clear()
                }
            }
        }
    }

    private fun setAdapter() {
        _adapter = PrivateChatAdapter()
        binding.chatRv.apply {
            (this.layoutManager as LinearLayoutManager).stackFromEnd = true
            adapter = _adapter
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).app_toolbar?.let {
            it.back_iv.also {iv->
                iv.visibility = View.VISIBLE
                iv.setOnClickListener {
                    backToChatList()
                }
            }
            it.card_view.visibility = View.VISIBLE
            it.title_tv.text = args.contact.nickname
            Glide.with(it)
                .load(args.contact.avatar)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(it.avatar_iv)
        }
    }

    private fun backToChatList() {
        val action = PrivateChatFragmentDirections.actionPrivateChatFragmentToChatListFragment()
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        CONTACT_ID = args.contact.uid!!
    }

    override fun onStop() {
        super.onStop()
        CONTACT_ID = null
    }

    private fun setViewModel() {
        viewModel.chat.onEach {
            _adapter.submitList(it)
            binding.chatRv.smoothScrollToPosition(_adapter.itemCount)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}