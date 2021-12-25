package com.example.mymessenger.fragments.login
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentLoginBinding
import com.example.mymessenger.utills.Constants
import com.example.mymessenger.utills.Constants.USER_NAME
import com.example.mymessenger.utills.State
import com.example.mymessenger.utills.launchWhenStarted
import com.example.mymessenger.utills.logz
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onStart() {
        super.onStart()
//        if(loginViewModel.isSigned()){
//            navigateToChatList()
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setLoginLayout()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setLoginLayout() {
        binding.root.also {
            it.getViewById(R.id.signup).visibility = View.INVISIBLE
            it.getViewById(R.id.login).visibility = View.VISIBLE
        }
        binding.login.btnLogin.setOnClickListener {
            if(checkFields()){
                val email = binding.login.etEmail.text.toString()
                val password = binding.login.etPassword.text.toString()
                loginViewModel.signIn(email, password).onEach {state->
                    when(state){
                        is State.Error ->{
                            "error".logz()
                            showToast(state.exception.toString())
                        }
                        is State.Waiting->{
                            //show loading
                        }
                        is State.Success ->{
                            "state success!".logz()
                            navigateToChatList()
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

        binding.login.txtSignup.setOnClickListener {
            setSignUpLayout()
        }
    }

    private fun navigateToChatList() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToChatListFragment()
        )
    }

    private fun setSignUpLayout() {
        binding.root.also {
            it.getViewById(R.id.login).visibility = View.INVISIBLE
            it.getViewById(R.id.signup).visibility = View.VISIBLE
        }
        binding.signup.txtLogin.setOnClickListener {
            setLoginLayout()
        }
    }


    private fun checkFields(): Boolean {
        val email = binding.login.etEmail.text.toString()
        val password = binding.login.etPassword.text.toString()
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