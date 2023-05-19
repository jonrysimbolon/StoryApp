package com.storyapp.fragment.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.storyapp.R
import com.storyapp.adapter.StoryAdapter
import com.storyapp.databinding.FragmentHomeBinding
import com.storyapp.main.MainViewModel
import com.storyapp.model.StoryModel
import com.storyapp.remote.response.ResultStatus
import com.storyapp.remote.response.Story
import com.storyapp.utils.StoryFailureDialog
import com.storyapp.utils.convertStoryToStoryModel
import com.storyapp.utils.showSnackBarAppearBriefly
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment : Fragment() {

    private val storyAdapter: StoryAdapter by inject()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val mainViewModel: MainViewModel by activityViewModel()
    private val failureDialog by lazy { StoryFailureDialog(requireActivity()) }
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val layoutManager = LinearLayoutManager(requireActivity())
            rvStory.layoutManager = layoutManager

            menuOverlay()

            srlMain.setOnRefreshListener {
                observeStories()
            }

            observeStories()

            homeViewModel.failure.observe(viewLifecycleOwner) { errorDialogModel ->
                failureDialog.show(errorDialogModel.show)
                failureDialog.setDescription(errorDialogModel.description)
                failureDialog.setOnClick {
                    observeStories()
                }
            }
        }
    }

    private fun observeStories() {
        binding.apply {
            homeViewModel.fetchStories().observe(viewLifecycleOwner) { result ->
                when (result) {
                    ResultStatus.Loading -> {
                        loadingState()
                    }

                    is ResultStatus.Error -> {
                        errorState(result.error)
                    }

                    is ResultStatus.Success -> {
                        successState()
                        result.data.message.showSnackBarAppearBriefly(root)
                        whatNext(result.data.listStory)
                    }
                }
            }
        }
    }

    private fun loadingState() {
        mainViewModel.showLoading(true)
        showErrorImg(false)
    }

    private fun errorState(error: String) {
        stopLoading()
        showErrorImg(true, error)
    }

    private fun successState() {
        stopLoading()
        showErrorImg(false)
    }

    private fun showErrorImg(show: Boolean, desc: String = "") {
        homeViewModel.showFailureImage(show, desc)
    }

    private fun stopLoading() {
        binding.apply {
            if (srlMain.isRefreshing) {
                srlMain.isRefreshing = false
            }
        }
        mainViewModel.showLoading(false)
    }

    private fun whatNext(listStory: List<Story>) {
        val storyData = ArrayList<StoryModel>()
        for (story in listStory) {
            storyData.add(
                convertStoryToStoryModel(story)
            )
        }
        storyAdapter.setListStories(storyData)
        storyAdapter.onClickItem = ::onClickItem
        binding.rvStory.adapter = storyAdapter
    }

    private fun onClickItem(viewHolder: StoryAdapter.ViewHolder, storyModel: StoryModel) {
        val toDetailStoryFragment = HomeFragmentDirections.actionHomeFragmentToDetailStoryFragment(
            storyModel.photoUrl,
            storyModel.createdAt,
            storyModel.name,
            storyModel.description
        )
        viewHolder
            .itemView
            .findNavController()
            .navigate(
                toDetailStoryFragment
            )
    }

    private fun menuOverlay() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.story_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.addNews -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddStoryFragment())
                        false
                    }

                    R.id.changeLanguage -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }

                    R.id.logout -> {
                        homeViewModel.logout()
                        getString(R.string.success_logout).showSnackBarAppearBriefly(binding.root)
                        true
                    }

                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}