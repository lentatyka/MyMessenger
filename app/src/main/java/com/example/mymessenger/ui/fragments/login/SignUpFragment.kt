package com.example.mymessenger.ui.fragments.login

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.HORIZONTAL
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentSignUpBinding
import com.example.mymessenger.utills.*
import com.example.mymessenger.viewmodels.LoginViewModel
import com.example.mymessenger.viewmodels.LoginViewModel.Companion.ERROR_EMAIL_ALREADY_IN_USE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var cameraActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if (result.resultCode == Activity.RESULT_OK) {
                    setAvatar(
                        result.data!!.extras!!.get("data") as Bitmap
                    )
            }
        }
        setViewModel()
        setLayout()
    }

    private fun setViewModel() {
        loginViewModel.state.onEach { state ->
            when (state) {
                is State.Success -> {
                    getString(R.string.confirm_email).showSnackBar(binding.root) {
                        findNavController().navigate(
                            SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
                        )
                    }
                }
                is State.Error -> {
                    when (state.error.errorCode) {
                        ERROR_EMAIL_ALREADY_IN_USE ->
                            binding.lEmail.error =
                                getString(R.string.error_email_in_use)
                        else ->
                            getString(R.string.error_unknown).showToast(requireContext())
                    }
                }
                is State.Loading -> {
                    //show loading
                }
                else -> {
                    //nothing
                }
            }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun setLayout() {
        binding.apply {
            lEmail.setListener(getString(R.string.email_error), etEmail) {
                loginViewModel.isValidEmail(etEmail.text.toString())
            }
            lNickname.setListener(getString(R.string.nickname_error), etNickname) {
                loginViewModel.isValidPasswordOrNickname(etNickname.text.toString())
            }
            lPassword.setListener(getString(R.string.invalid_password_error), etPassword) {
                loginViewModel.isValidPassword(etPassword.text.toString())
            }
            lConfirmPassword.setListener(getString(R.string.c_password_error), etConfirmPassword) {
                loginViewModel.isValidConfirmPassword(
                    etConfirmPassword.text.toString(),
                    etPassword.text.toString()
                )
            }
            setAvatar(loginViewModel.getAvatar())

            val imageAssets = resources.assets.list("avatars")
            val list = mutableListOf<Bitmap?>()
            //Just for camera
            list += null
            imageAssets?.forEach { s ->
                list += BitmapFactory.decodeStream(resources.assets.open("avatars/$s"))
            }
            avatarRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
                adapter = AvatarAdapter(list){
                    it?.let {
                        setAvatar(it)
                        loginViewModel.setAvatar(it)
                    } ?: startCamera()
                }
            }
            btnSignUp.setOnClickListener {
                if (checkSignUpFields()) {
                    val nickname = binding.etNickname.text.toString()
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()
                    loginViewModel.signUp(email, password, nickname)
                } else {
                    getString(R.string.invalid_data).showToast(requireContext())
                }
            }
        }
    }

    private fun setAvatar(avatar: Bitmap){
        Glide.with(this)
            .load(avatar)
            .centerCrop()
            .into(binding.avatarLayout.avatarIv)
    }

    private fun startCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            cameraActivity.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            "$e".showToast(requireContext())
        }
    }

    private fun checkSignUpFields(): Boolean {
        binding.apply {
            val nickname = etNickname.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val cPassword = etConfirmPassword.text.toString()
            return loginViewModel.isSignUpValid(nickname, email, password, cPassword)
        }
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