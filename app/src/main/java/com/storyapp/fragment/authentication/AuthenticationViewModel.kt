package com.storyapp.fragment.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthenticationViewModel : ViewModel() {
    private val _selectedFragmentIndex = MutableLiveData<Int>()
    val selectedFragmentIndex: LiveData<Int> = _selectedFragmentIndex

    fun switchToFragment(index: Int) {
        _selectedFragmentIndex.value = index
    }
}