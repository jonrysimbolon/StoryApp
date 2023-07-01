package com.jonrysimbolonstory.fragment.add

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.jonrysimbolonstory.R
import com.jonrysimbolonstory.databinding.FragmentAddStoryBinding
import com.jonrysimbolonstory.remote.response.Response
import com.jonrysimbolonstory.utils.Event
import com.jonrysimbolonstory.utils.LoadingDialog
import com.jonrysimbolonstory.utils.PREFIX_FILE
import com.jonrysimbolonstory.utils.ResultStatus
import com.jonrysimbolonstory.utils.SUFFIX_FILE
import com.jonrysimbolonstory.utils.dateFormatFromServer
import com.jonrysimbolonstory.utils.descImage
import com.jonrysimbolonstory.utils.isValidAddPhoto
import com.jonrysimbolonstory.utils.showSnackBarAppearBriefly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryFragment : Fragment() {

    private val binding by lazy { FragmentAddStoryBinding.inflate(layoutInflater) }
    private val addStoryViewModel: AddStoryViewModel by activityViewModel()
    private val loadingDialog: LoadingDialog by inject()
    private var fileAddPhoto: File? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
            setMinUpdateDistanceMeters(minimalDistance)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    lat = location.latitude
                    lon = location.longitude
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog.init(requireContext())
        permissionAddPhoto()
        observer()

        binding.apply {
            locationCb.setOnCheckedChangeListener { _, isChecked ->
                addStoryViewModel.enableCheckbox(isChecked)
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
                        getString(R.string.empty_desc),
                        locationCb,
                        lat, lon,
                        { error ->
                            if (error) {
                                getString(R.string.location_null).showSnackBarAppearBriefly(
                                    root
                                )
                            }
                        }
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
                            descImage(edAddDescription.text.toString()),
                            lat, lon
                        )
                    }
                }
            }
        }
    }


    private fun observer() {
        addStoryViewModel.checkBoxState.observe(viewLifecycleOwner) { enable ->
            if (enable) {
                createLocationRequest()
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
        }

        addStoryViewModel.buttonState.observe(viewLifecycleOwner) { enable ->
            binding.apply {
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
        }

        val observer = Observer<Event<ResultStatus<Response>>> { value ->
            value.getContentIfNotHandled()?.let { resultStatus ->
                when (resultStatus) {
                    is ResultStatus.Loading -> {
                        loadingDialog.show()
                        addStoryViewModel.enableButton(false)
                    }

                    is ResultStatus.Error -> {
                        addStoryViewModel.enableButton(true)
                        loadingDialog.show(false)
                        resultStatus.error.showSnackBarAppearBriefly(binding.root)
                    }

                    is ResultStatus.Success -> {
                        addStoryViewModel.enableButton(true)
                        loadingDialog.show(false)
                        val message = resultStatus.data.message
                        whatNext(message)
                    }
                }
            }
        }

        addStoryViewModel.addStoryLiveData.observe(viewLifecycleOwner, observer)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {}
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                } else {
                    getString(R.string.location_not_found).showSnackBarAppearBriefly(binding.root)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {}
                RESULT_CANCELED ->
                    getString(R.string.must_active_gps).showSnackBarAppearBriefly(binding.root)
            }
        }

    private fun createLocationRequest() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.message.toString().showSnackBarAppearBriefly(binding.root)
                    }
                }
            }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error : " + exception.message.toString())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        lat = null
        lon = null
    }

    override fun onResume() {
        super.onResume()
        addStoryViewModel.checkBoxState.observe(viewLifecycleOwner) { enable ->
            if (enable) {
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onDestroyView() {
        addStoryViewModel.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
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
                getString(R.string.not_get_permission).showSnackBarAppearBriefly(binding.root)
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
        private const val TAG = "AddStoryFragment"
        private const val minimalDistance = 100F
        private const val timeInterval = 5000L
    }

}