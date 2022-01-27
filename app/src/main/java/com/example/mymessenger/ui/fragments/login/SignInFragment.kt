package com.example.mymessenger.ui.fragments.login
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentSignInBinding
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.*
import com.example.mymessenger.utills.Constants.USER_ID
import com.example.mymessenger.viewmodels.LoginViewModel
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_EMAIL_NOT_VERIFIED
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_USER_NOT_FOUND
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_WRONG_PASSWORD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        setSignInLayout()
    }

    private fun setSignInLayout() {
        binding.apply {
            lEmail.setListener(getString(R.string.error_email_not_verified), etEmail){
                loginViewModel.isValidEmail(etEmail.text.toString())
            }
            lPassword.setListener(getString(R.string.password_error), etPassword){
                loginViewModel.isValidPasswordOrNickname(etPassword.text.toString())
            }
            txtSignup.setOnClickListener {
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
            }
            txtForgot.setOnClickListener {
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToResetPasswordFragment())
            }
            btnSignIn.setOnClickListener {
                binding.btnSignIn.requestFocus()
                if(checkSignInFields()){
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()
                    loginViewModel.signIn(email, password)
                }else{
                    getString(R.string.invalid_data).showToast(requireContext())
                }
            }
        }
    }

    private fun setViewModel(){
        loginViewModel.state.onEach {state->
            when(state){
                is State.Error ->{
                    when(state.error.errorCode){
                        ERROR_USER_NOT_FOUND ->
                            binding.lEmail.error = getString(R.string.error_user_not_found)
                        ERROR_EMAIL_NOT_VERIFIED ->
                            getString(R.string.error_email_not_verified).showSnackBar(binding.root){
                                loginViewModel.sendEmailVerification()
                                getString(R.string.sent_email).showToast(requireContext())
                            }
                        ERROR_WRONG_PASSWORD ->{
                            binding.also {
                                it.etPassword.text?.clear()
                                it.lPassword.error = getString(R.string.error_wrong_password)
                            }
                        }
                        else-> getString(R.string.error_unknown).showToast(requireContext())
                    }
                }
                is State.Loading->{
                    //show loading
                }
                is State.Success ->{
                    navigateToChatList()
                }
                else->{
                    //nothing
                }
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun navigateToChatList() {
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            putString(getString(R.string.user_id), USER_ID)
            apply()
        }
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(intent)
    }

    private fun checkSignInFields(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        return loginViewModel.isSignInValid(email, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.setState(State.Waiting)
    }
}