package com.example.mymessenger.fragment_login
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentLoginBinding
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.launchWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAuth.setOnClickListener {
            if(checkFields()){
                Constants.EMAIL = binding.etEmail.text.toString()
                Constants.PASSWORD = binding.etPassword.text.toString()
                loginViewModel.signIn().onEach {state->
                    when(state){
                        is State.Success ->{
                            findNavController()
                                .navigate(R.id.action_loginFragment_to_chatListFragment)
                        }
                        is State.Error ->{
                            showToast(state.exception.toString())
                        }
                        else->{
                            //nothing
                        }
                    }
                }.launchWhenStarted(lifecycleScope)
            }
            else
                showToast("Bad fields data")
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkFields(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        return loginViewModel.isDataValid(email, password)
    }

    private fun showToast(text: String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}