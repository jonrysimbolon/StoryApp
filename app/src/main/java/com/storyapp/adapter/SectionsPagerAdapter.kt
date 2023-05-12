package com.storyapp.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.storyapp.fragment.authentication.login.LoginFragment
import com.storyapp.fragment.authentication.registration.RegistrationFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> LoginFragment()
            1 -> RegistrationFragment()
            else -> Fragment()
        }

    override fun getItemCount(): Int = 2
}