package com.storyapp.fragment.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.storyapp.R
import com.storyapp.databinding.FragmentAddStoryBinding
import com.storyapp.main.MainViewModel
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.PREFIX_FILE
import com.storyapp.utils.SUFFIX_FILE
import com.storyapp.utils.dateFormatFromServer
import com.storyapp.utils.descImage
import com.storyapp.utils.isValidAddPhoto
import com.storyapp.utils.showSnackBarAppearBriefly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryFragment : Fragment() {

    private val binding by lazy { FragmentAddStoryBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by activityViewModel()
    private val addStoryViewModel: AddStoryViewModel by activityViewModel()
    private var fileAddPhoto: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionAddPhoto()
        binding.apply {
            addStoryViewModel.buttonState.observe(viewLifecycleOwner) { enable ->
                buttonAddPhoto.isEnabled = enable
                if (enable) {
                    buttonAddPhoto.text = getString(R.string.add_photo)
                    buttonAddPhoto.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_500
                        )
                    )
                } else {
                    buttonAddPhoto.text = getString(R.string.please_wait)
                    buttonAddPhoto.setBackgroundColor(Color.GRAY)
                }
            }

            ivAddPhoto.setOnClickListener {
                showPopup()
            }

            buttonAddPhoto.setOnClickListener {
                if (!isValidAddPhoto(
                        fileAddPhoto,
                        edAddDescription,
                        { error ->
                            if (error) {
                                getString(R.string.file_null).showSnackBarAppearBriefly(
                                    root
                                )
                            }
                        },
                        getString(R.string.empty_desc)
                    )
                ) {
                    return@setOnClickListener
                }

                addStoryViewModel.enableButton(false)
                val multipartBody = lifecycleScope.async {
                    try {
                        addStoryViewModel.processSelectedImage(fileAddPhoto)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    if (multipartBody.await() != null) {
                        addStoryViewModel.addStory(
                            multipartBody.await() ?: return@launch,
                            descImage(edAddDescription.text.toString())
                        ).observe(viewLifecycleOwner) { result ->
                            when (result) {
                                ResultStatus.Loading -> {
                                    mainViewModel.showLoading(true)
                                    addStoryViewModel.enableButton(false)
                                }

                                is ResultStatus.Error -> {
                                    addStoryViewModel.enableButton(true)
                                    mainViewModel.showLoading(false)
                                    result.error.showSnackBarAppearBriefly(root)
                                }

                                is ResultStatus.Success -> {
                                    addStoryViewModel.enableButton(true)
                                    mainViewModel.showLoading(false)
                                    result.data.message.showSnackBarAppearBriefly(root)
                                    whatNext(result.data.message)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun whatNext(message: String) {
        message.showSnackBarAppearBriefly(binding.root)
        findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToHomeFragment())
    }

    private fun showPopup() {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.select_option))
            setItems(
                arrayOf(
                    getString(R.string.take_photo),
                    getString(R.string.choose_gallery)
                )
            ) { _, which ->
                when (which) {
                    REQUEST_IMAGE_CAPTURE -> camera()
                    REQUEST_IMAGE_PICK -> gallery()
                }
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }
            .create()
            .show()
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                fileAddPhoto = file
                binding.ivAddPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = addStoryViewModel.uriToFile(uri, requireActivity())
                fileAddPhoto = myFile
                binding.ivAddPhoto.setImageURI(uri)
            }
        }
    }

    private fun gallery() {
        launcherIntentGallery.launch(
            Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = TYPE_IMAGE
            }.also {
                Intent.createChooser(it, getString(R.string.choose_picture))
            }
        )
    }

    private fun camera() {
        val photoFile = File.createTempFile(
            "$PREFIX_FILE${
                SimpleDateFormat(
                    dateFormatFromServer,
                    Locale.getDefault()
                ).format(Date())
            }",
            SUFFIX_FILE,
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        val photoUri = FileProvider.getUriForFile(
            requireActivity(),
            requireActivity().packageName,
            photoFile
        )
        currentPhotoPath = photoFile.absolutePath

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        launcherIntentCamera.launch(cameraIntent)
    }

    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.not_get_permission),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }.launch(cameraPermission)
    }

    private fun permissionAddPhoto() {
        if (!allPermissionsGranted()) {
            requestCameraPermission()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_IMAGE_CAPTURE = 0
        private const val REQUEST_IMAGE_PICK = 1
        private const val TYPE_IMAGE = "image/*"
    }

}