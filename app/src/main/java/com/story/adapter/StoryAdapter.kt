package com.story.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.story.databinding.ItemStoryBinding
import com.story.model.StoryModel
import com.story.utils.dateFormat

class StoryAdapter(
    private val glide: RequestManager,
    var onClickItem: ((ViewHolder, StoryModel) -> Unit)? = null
) : PagingDataAdapter<StoryModel, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryModel) {
            binding.apply {
                with(item) {
                    tvItemCreateDate.text = dateFormat(createdAt)
                    tvItemName.text = name
                    tvItemDesc.text = description
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item)

        glide.load(item?.photoUrl)
            .into(holder.binding.ivItemPhoto)

        holder.itemView.setOnClickListener {
            onClickItem?.let { onClickItem ->
                getItem(position)?.let { storyModel ->
                    onClickItem(holder, storyModel)
                }
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryModel>() {
            override fun areItemsTheSame(
                oldItem: StoryModel,
                newItem: StoryModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryModel,
                newItem: StoryModel
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}