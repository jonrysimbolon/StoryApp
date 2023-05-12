package com.storyapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.storyapp.remote.response.Story

class StoryDiffCallback(
    private val mOldStoryList: List<Story>,
    private val mNewStoryList: List<Story>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = mOldStoryList.size

    override fun getNewListSize(): Int = mNewStoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        mOldStoryList[oldItemPosition].id == mNewStoryList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = mOldStoryList[oldItemPosition]
        val newUser = mNewStoryList[newItemPosition]

        return oldUser.id == newUser.id && oldUser.name == newUser.name
    }

}