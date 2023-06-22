package com.story.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.story.databinding.FragmentDetailStoryBinding
import com.story.utils.dateFormat
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DetailStoryFragment : Fragment() {

    private var imageStory: String = ""
    private var dateStory: String = ""
    private var nameStory: String = ""
    private var descStory: String = ""

    private val binding by lazy { FragmentDetailStoryBinding.inflate(layoutInflater) }
    private val viewModel: DetailStoryViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DetailStoryFragmentArgs.fromBundle(arguments as Bundle).apply {
            this@DetailStoryFragment.imageStory = imageStory
            this@DetailStoryFragment.dateStory = dateStory
            this@DetailStoryFragment.nameStory = nameStory
            this@DetailStoryFragment.descStory = descStory
        }

        binding.apply {
            viewModel.setImageToView(imageStory, ivDetailPhoto)
            tvItemCreateDate.text = dateFormat(dateStory)
            tvDetailName.text = nameStory
            tvDetailDescription.text = descStory
        }
    }

}