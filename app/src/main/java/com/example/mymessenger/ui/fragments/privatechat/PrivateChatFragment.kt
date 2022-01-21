package com.example.mymessenger.ui.fragments.privatechat

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentPrivateChatBinding
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.Constants.CONTACT_ID
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.example.mymessenger.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.avatar_item.*
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PrivateChatFragment : Fragment() {
    private val args: PrivateChatFragmentArgs by navArgs()
    private var _binding: FragmentPrivateChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var _adapter: PrivateChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        (activity as MainActivity).supportActionBar?.let {
            val view = it.customView
            view.findViewById<CardView>(R.id.avatar_card).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.title_tv).text = args.contact.nickname
            it.setDisplayHomeAsUpEnabled(true)
            Glide.with(view)
                .load(args.contact.avatar)
                .centerCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(view.findViewById(R.id.avatar_iv))
        }
    }

    private fun setViewModel() {
        viewModel.getChat(args.contact.uid!!).onEach {
            _adapter.submitList(it)
            binding.chatRv.smoothScrollToPosition(_adapter.itemCount)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            findNavController().navigate(
                PrivateChatFragmentDirections.actionPrivateChatFragmentToChatListFragment()
            )
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        CONTACT_ID = args.contact.uid!!
    }

    override fun onStop() {
        super.onStop()
        CONTACT_ID = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}