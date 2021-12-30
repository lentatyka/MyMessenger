package com.example.mymessenger.ui.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentResetPasswordBinding
import com.example.mymessenger.utills.*
import com.example.mymessenger.viewmodels.LoginViewModel
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_USER_NOT_FOUND
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private var _binding:FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setForgotLayout()
    }

        private fun setForgotLayout(){
        binding.apply {
            lEmail.setListener(getString(R.string.email_error), etEmail){
                loginViewModel.isValidEmail(etEmail.text.toString())
            }
                btnSend.setOnClickListener {
                    it.requestFocus()
                    val email = etEmail.text.toString()
                    if(loginViewModel.isValidEmail(email)){
                        loginViewModel.resetPassword(email).onEach {state->
                            when(state){
                                is State.Success->{
                                    getString(R.string.sent_email).showSnackBar(binding.root){
                                        findNavController().navigate(
                                            ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment()
                                        )
                                    }
                                }
                                is State.Loading->{
                                    //loading
                                }
                                is State.Error->{
                                    when(state.error.errorCode){
                                        ERROR_USER_NOT_FOUND ->
                                            binding.lEmail.error = getString(R.string.error_user_not_found)
                                        else->{
                                            getString(R.string.error_unknown).showToast(requireContext())
                                        }
                                    }
                                }
                            }
                        }.launchWhenStarted(lifecycleScope)
                    }else{
                        getString(R.string.invalid_data).showToast(requireContext())
                    }
                }
            }
        }
    }
