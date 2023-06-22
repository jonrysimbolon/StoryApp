package com.story.fragment.home

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.story.R
import com.story.adapter.FooterLoadingStateAdapter
import com.story.adapter.StoryAdapter
import com.story.databinding.FragmentHomeBinding
import com.story.model.StoryModel
import com.story.utils.LoadingDialog
import com.story.utils.StoryFailureDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment : Fragment() {

    private val storyAdapter: StoryAdapter by inject()
    private val loadingDialog: LoadingDialog by inject()
    private val failureDialog: StoryFailureDialog by inject()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val layoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuOverlay()
        homeViewModel.story.observe(viewLifecycleOwner) { storyData ->
            storyData?.let {
                storyAdapter.submitData(lifecycle, it)
                storyAdapter.notifyItemChanged(TOP_POSITION)
            }
        }

        binding.apply {
            loadingDialog.init(requireContext())
            failureDialog.init(requireContext())

            rvStory.layoutManager = layoutManager
            storyAdapter.onClickItem = ::onClickItem

            storyAdapter.addLoadStateListener { loadState ->
                val mediatorLoadState =
                    loadState.mediator?.refresh
                when (mediatorLoadState) {
                    is LoadState.NotLoading -> {
                        loadingDialog.show(false)
                    }

                    is LoadState.Loading -> {
                        loadingDialog.show()
                    }

                    is LoadState.Error -> {
                        loadingDialog.show(false)
                        failureDialog.apply {
                            show()
                            setDescription(mediatorLoadState.error.message.toString())
                            setReloadClickListener {
                                storyAdapter.refresh()
                            }
                            setLogoutClickListener {
                                logoutAct()
                            }
                        }
                    }

                    else -> loadingDialog.show(false)
                }
            }

            rvStory.adapter = storyAdapter.withLoadStateFooter(
                footer = FooterLoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onResume() {
        super.onResume()
        homeViewModel.observeLoginState()
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

                    R.id.mapStory -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStoryMapsFragment())
                        false
                    }

                    R.id.changeLanguage -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }

                    R.id.logout -> {
                        logoutAct()
                        true
                    }

                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun logoutAct() {
        homeViewModel.logout()
    }

    companion object {
        const val TOP_POSITION = 0
    }
}