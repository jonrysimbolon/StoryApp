package com.storyapp.fragment.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.storyapp.R
import com.storyapp.adapter.SectionsPagerAdapter
import com.storyapp.databinding.FragmentAuthenticationBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AuthenticationFragment : Fragment() {

    private val binding by lazy { FragmentAuthenticationBinding.inflate(layoutInflater) }
    private val authViewModel: AuthenticationViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity() as AppCompatActivity)
            viewPager.adapter = sectionsPagerAdapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()

            authViewModel.selectedFragmentIndex.observe(viewLifecycleOwner) { index ->
                viewPager.setCurrentItem(index, true)
            }
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.login_page,
            R.string.registration_page
        )
    }

}