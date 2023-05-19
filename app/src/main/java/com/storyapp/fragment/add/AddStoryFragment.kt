package com.storyapp.fragment.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.storyapp.R
import com.storyapp.databinding.FragmentAddStoryBinding
import com.storyapp.main.MainViewModel
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.isValidAddPhoto
import com.storyapp.utils.reduceFileImage
import com.storyapp.utils.showSnackBarAppearBriefly
import com.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

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
            ivAddPhoto.setOnClickListener {
                showPopup()
            }

            buttonAddPhoto.setOnClickListener {
                if (isValidAddPhoto(
                        fileAddPhoto,
                        edAddDescription,
                        { error ->
                            if (error) {
                                getString(R.string.file_null).showSnackBarAppearBriefly(
                                    root
                                )
                            }
                        },
                        { getString(R.string.invalid_desc_add_photo) }
                    )
                ) {

                    val file = reduceFileImage(fileAddPhoto as File)
                    val description = edAddDescription.text.toString().toRequestBody(TEXT_PLAIN_TYPE.toMediaType())
                    val requestImageFile = file.asRequestBody(REQUEST_TYPE_IMAGE.toMediaType())
                    val imageMultipart = MultipartBody.Part.createFormData(
                        PHOTO,
                        file.name,
                        requestImageFile
                    )
                    addStoryViewModel.addStory(
                        imageMultipart,
                        description
                    ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            ResultStatus.Loading -> mainViewModel.showLoading(true)

                            is ResultStatus.Error -> {
                                mainViewModel.showLoading(false)
                                result.error.showSnackBarAppearBriefly(root)
                            }

                            is ResultStatus.Success -> {
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

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == CAMERA_X_RESULT){
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.data?.getSerializableExtra(PICTURE, File::class.java)
            }else{
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra(PICTURE)
            } as? File
            // val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                // rotateFile(file, isBackCamera) // gk ngerti apa maksudnya, soalnya di hp saya jadi mutar kalo pake ini
                fileAddPhoto = file
                binding.ivAddPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireActivity())
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
        private const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_IMAGE_CAPTURE = 0
        private const val REQUEST_IMAGE_PICK = 1
        private const val TYPE_IMAGE = "image/*"
        private const val REQUEST_TYPE_IMAGE = "image/jpeg"
        private const val TEXT_PLAIN_TYPE = "text/plain"
        private const val PHOTO = "photo"
        private const val PICTURE = "picture"
    }

}