package com.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.storyapp.databinding.ItemStoryBinding
import com.storyapp.model.StoryModel
import com.storyapp.utils.dateFormat

class StoryAdapter(
    private val glide: RequestManager,
    var onClickItem: ((ViewHolder, StoryModel) -> Unit)? = null
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    private val stories: MutableList<StoryModel> = mutableListOf()

    fun setListStories(stories: List<StoryModel>) {
        val diffCallback = StoryDiffCallback(this.stories, stories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.stories.clear()
        this.stories.addAll(stories)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryModel) {
            with(item) {
                binding.tvItemCreateDate.text = dateFormat(createdAt)
                binding.tvItemName.text = name
                binding.tvItemDesc.text = description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = stories[position]
        holder.bind(item)

        glide.load(item.photoUrl)
            .into(holder.binding.ivItemPhoto)

        holder.itemView.setOnClickListener {
            onClickItem?.let { onClickItem ->
                onClickItem(holder, stories[position])
            }
        }
    }
}