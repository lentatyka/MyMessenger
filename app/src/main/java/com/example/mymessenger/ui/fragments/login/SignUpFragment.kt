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
import com.example.mymessenger.databinding.FragmentSignUpBinding
import com.example.mymessenger.utills.*
import com.example.mymessenger.viewmodels.LoginViewModel
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_EMAIL_ALREADY_IN_USE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding:FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSignUpLayout()
    }

        private fun setSignUpLayout() {
        binding.apply {
            lEmail.setListener(getString(R.string.email_error), etEmail){
                loginViewModel.isValidEmail(etEmail.text.toString())
            }
            lNickname.setListener(getString(R.string.nickname_error), etNickname){
                loginViewModel.isValidPasswordOrNickname(etNickname.text.toString())
            }
            lPassword.setListener(getString(R.string.invalid_password_error), etPassword){
                loginViewModel.isValidPassword(etPassword.text.toString())
            }
            lConfirmPassword.setListener(getString(R.string.c_password_error), etConfirmPassword){
                loginViewModel.isValidConfirmPassword(
                    etConfirmPassword.text.toString(),
                    etPassword.text.toString()
                )
            }
            btnSignUp.setOnClickListener {
                it.requestFocus()
                if(checkSignUpFields()){
                    val nickname = binding.etNickname.text.toString()
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()
                    loginViewModel.signUp(email, password, nickname).onEach {state->
                        when(state){
                            is State.Success ->{
                                getString(R.string.confirm_email).showSnackBar(binding.root){
                                    findNavController().navigate(
                                        SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
                                    )
                                }
                            }
                            is State.Error ->{
                                when(state.error.errorCode){
                                    ERROR_EMAIL_ALREADY_IN_USE ->
                                        binding.lEmail.error = getString(R.string.error_email_in_use)
                                    else->
                                        getString(R.string.error_unknown).showToast(requireContext())
                                }
                            }
                            is State.Loading->{
                                //show loading
                            }
                            else->{
                                //nothing
                            }
                        }
                    }.launchWhenStarted(lifecycleScope)
                }else{
                    getString(R.string.invalid_data).showToast(requireContext())
                }
            }
        }
    }

        private fun checkSignUpFields():Boolean{
        binding.apply {
            val nickname = etNickname.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val c_password = etConfirmPassword.text.toString()
            return loginViewModel.isSignUpValid(nickname, email, password, c_password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}