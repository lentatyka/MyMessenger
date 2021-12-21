package com.example.mymessenger.fragments.privatechat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.databinding.FragmentPrivateChatBinding
import com.example.mymessenger.utills.Constants.CONTACT_ID
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PrivateChatFragment : Fragment() {
    private val args: PrivateChatFragmentArgs by navArgs()
    private var _binding: FragmentPrivateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter_: PrivateChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivateChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter_ = PrivateChatAdapter()
        binding.chatRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (adapter_)
        }
        setViewModel()
        binding.enterBtn.setOnClickListener {
            binding.messageEt.apply {
                viewModel.sendMessage(text.toString(), args.contact)
                text.clear()
            }
        }
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
        viewModel.getPrivateChat(args.contact.uid!!).onEach {
            adapter_.submitList(it)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}