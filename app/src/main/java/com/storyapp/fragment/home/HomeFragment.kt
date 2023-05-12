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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.storyapp.R
import com.storyapp.databinding.FragmentHomeBinding
import com.storyapp.main.MainViewModel
import com.storyapp.remote.response.ResultStatus
import com.storyapp.remote.response.Story
import com.storyapp.utils.showSnackBarAppearBriefly
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.isLogin().observe(viewLifecycleOwner) { isLogin ->
            binding.homeLayout.visibility = if (isLogin) View.VISIBLE else View.GONE
        }

        binding.apply {
            menuOverlay()

            homeViewModel.getUser().observe(viewLifecycleOwner){ userModel ->
                home2Id.text = userModel.toString()
            }

            homeViewModel.fetchStories().observe(viewLifecycleOwner){ result ->
                when (result) {
                    ResultStatus.Loading -> mainViewModel.showLoading(true)

                    is ResultStatus.Error -> {
                        mainViewModel.showLoading(false)
                        result.error.showSnackBarAppearBriefly(root)
                    }

                    is ResultStatus.Success -> {
                        mainViewModel.showLoading(false)
                        result.data.message.showSnackBarAppearBriefly(root)
                        whatNext(result.data.listStory)
                    }
                }
            }
        }
    }

    private fun whatNext(listStory: List<Story>) {

    }

    private fun menuOverlay() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.story_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.addNews -> {
                        getString(R.string.add_news).showSnackBarAppearBriefly(binding.root)
                        false
                    }

                    R.id.changeLanguage -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }
                    R.id.logout ->{
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