package com.example.mymessenger.f_privatechat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymessenger.MyService
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentPrivateChatBinding
import com.example.mymessenger.interfaces.Message
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.MainViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PrivateChatFragment : Fragment() {
    private val args: PrivateChatFragmentArgs by navArgs()
    private var _binding: FragmentPrivateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrivateChatViewModel by viewModels()
    private lateinit var gpAdapter: GroupAdapter<GroupieViewHolder>
    lateinit var observer: Observer<Message?>

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
        gpAdapter = GroupAdapter<GroupieViewHolder>()
        binding.chatRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = (gpAdapter)
        }
        setViewModel()
        binding.enterBtn.setOnClickListener {
            binding.messageEt.apply {
                viewModel.sendMessage(text.toString(), args.contact)
                text.clear()
            }
        }
    }

    private fun setViewModel() {
        viewModel.privateChat.onEach {
            gpAdapter.update(it)
        }.launchWhenStarted(lifecycleScope)
        observer = Observer{ message ->
            "obserber said: $message".logz()
            message?.let {
                viewModel.updatePrivateChat(message)
            }
        }
        MyService.burning.onEach {
            "burning: $it".logz()
        }.launchWhenStarted(lifecycleScope)
        MyService.messages.observe(viewLifecycleOwner, observer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        MyService.messages.removeObserver(observer)
    }
}