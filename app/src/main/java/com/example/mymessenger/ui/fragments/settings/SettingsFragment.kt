package com.example.mymessenger.ui.fragments.settings

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mymessenger.R
import com.example.mymessenger.databinding.FragmentSettingsBinding
import com.example.mymessenger.ui.activities.MainActivity
import com.example.mymessenger.utills.*
import com.example.mymessenger.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel:SettingsViewModel by viewModels()
    @Inject
    lateinit var cameraResultContract: CameraResultContract
    lateinit var cameraActivity: ActivityResultLauncher<CameraResultContract.Action>
    lateinit var _menu:Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraActivity = registerForActivityResult(
            cameraResultContract
        ){result->
            result?.let {
                viewModel.setAvatar(it)
                _menu.findItem(R.id.item_save).isVisible = true
            }
        }
        setToolbar()
        setLayout()
        setViewModel()
    }

    private fun setToolbar() {
        (activity as MainActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            val view = it.customView
            view.findViewById<ShapeableImageView>(R.id.avatar_iv).visibility = View.GONE
            view.findViewById<TextView>(R.id.title_tv).text = getString(R.string.settings)
        }
    }

    private fun setLayout() {
        binding.cameraBtn.setOnClickListener {
            showCameraPanel()
        }
        binding.avatarLayout.setOnClickListener {
            binding.cameraBtn.also {
                it.visibility = if(it.isVisible) View.GONE else View.VISIBLE
            }
        }
        binding.fakeBtn.setOnClickListener {
            hideCameraPanel()
        }
        binding.cameraPanel.cameraIb.setOnClickListener {
            launchCameraOrGallery(CameraResultContract.Action.CAMERA)
        }
        binding.cameraPanel.galleryIb.setOnClickListener {
            launchCameraOrGallery(CameraResultContract.Action.GALLERY)
        }
        binding.editBtn.setOnClickListener {
            showEditNameDialog()
        }
    }

    private fun showEditNameDialog() {
        val view = layoutInflater.inflate(R.layout.edit_name, null)
        val name = view.findViewById<EditText>(R.id.name_et)
        MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.accept)){_, _->
                if(name.text.toString().isNotBlank()){
                    viewModel.setUserName(name.text.toString())
                    _menu.findItem(R.id.item_save).isVisible = true
                }
            }
            .show()
    }

    private fun showSaveDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.save))
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.accept)){_, _->
                viewModel.saveChanges()
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToChatListFragment()
                )
            }
            .show()
    }

    private fun setViewModel() {
        viewModel.user.onEach {user->
            binding.isVisible = user
            when(user){
                is State.Error ->{
                    val message = user.error.message ?: "Error"
                    message.showToast(requireContext())
                }
                is State.Object ->{
                    binding.user = user.contacts
                }
        }
        }.launchWhenStarted(lifecycleScope)
    }

    private fun showCameraPanel() {
        binding.camera = true
        binding.cameraPanel.cameraLayout
            .animate().translationY(-(binding.cameraPanel.cameraLayout.height).toFloat())
            .start()
    }

    private fun hideCameraPanel() {
        binding.cameraPanel.cameraLayout.animate().translationY(0f).start()
        binding.camera = false
    }
    private fun launchCameraOrGallery(action: CameraResultContract.Action) {
        try {
            cameraActivity.launch(action)
        }catch (e: IOException){
            "${e.message}".showToast(requireContext())
        }
        hideCameraPanel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settingsmenu, menu)
        _menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                findNavController().navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToChatListFragment()
                )
            }
            R.id.item_save ->{
                showSaveDialog()
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
@BindingAdapter("avatar_bind", "error")
fun loadAvatar(image: ImageView, bytes: ByteArray?, error: Drawable){
    Glide.with(image)
        .load(bytes)
        .error(error)
        .placeholder(error)
        .into(image)
}

