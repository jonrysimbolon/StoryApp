package com.story.fragment.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.story.R
import com.story.databinding.FragmentStoryMapsBinding
import com.story.model.StoryModel
import com.story.utils.LoadingDialog
import com.story.utils.ResultStatus
import com.story.utils.StoryFailureDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class StoryMapsFragment : Fragment() {

    private val storyMapsViewModel: StoryMapsViewModel by activityViewModel()
    private val binding by lazy { FragmentStoryMapsBinding.inflate(layoutInflater) }
    private val boundsBuilder = LatLngBounds.builder()
    private val loadingDialog: LoadingDialog by inject()
    private val storyFailureDialog: StoryFailureDialog by inject()
    private var listStory: List<StoryModel>? = null
    private var googleMap: GoogleMap? = null
    private var mapReady = false

    private val mapsCallback = OnMapReadyCallback { map ->
        googleMap = map
        mapReady = true
        updateMap()
        setMapStyle(map)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog.init(requireContext())
        storyFailureDialog.init(requireContext())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mapsCallback)
        observeStoryMap()
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style)
            )
            if (success)
                Log.e(TAG, STYLE_PARSING_FAILED)
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, CANT_FIND_STYLE_ERROR, exception)
        }
    }

    private fun observeStoryMap() {
        storyMapsViewModel.storyMap.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultStatus.Loading -> {
                    loadingDialog.show()
                }

                is ResultStatus.Success -> {
                    loadingDialog.show(false)
                    val storyMapResponse = result.data.listStory
                    listStory = storyMapResponse
                    updateMap()
                }

                is ResultStatus.Error -> {
                    loadingDialog.show(false)
                    storyFailureDialog.apply {
                        show()
                        setDescription(result.error)
                        setReloadClickListener {
                            storyMapsViewModel.fetchStoriesWithLocation()
                        }
                        setLogoutClickListener {
                            storyMapsViewModel.logout()
                        }
                    }
                }
            }
        }
    }

    private fun updateMap() {
        if (mapReady && listStory != null) {
            googleMap?.apply {
                uiSettings.apply {
                    isZoomControlsEnabled = true
                    isIndoorLevelPickerEnabled = true
                    isCompassEnabled = true
                    isMapToolbarEnabled = true

                    listStory?.forEach { story ->
                        val latLng = LatLng(story.lat, story.lon)
                        addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(story.description)
                        )
                        boundsBuilder.include(latLng)
                    }

                    val bounds = boundsBuilder.build()
                    animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            padding
                        )
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        const val padding = 300
        const val TAG = "StoryMapsFragment"
        const val CANT_FIND_STYLE_ERROR = "Can't find style. Error: "
        const val STYLE_PARSING_FAILED = "Style parsing failed."
    }

}