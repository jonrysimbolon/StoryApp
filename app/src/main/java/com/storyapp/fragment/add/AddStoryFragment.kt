package com.storyapp.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.storyapp.databinding.FragmentAddStoryBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddStoryFragment : Fragment() {

    private val binding by lazy { FragmentAddStoryBinding.inflate(layoutInflater) }
    private val viewModel: AddStoryViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}