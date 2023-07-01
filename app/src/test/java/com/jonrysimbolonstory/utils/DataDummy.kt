package com.jonrysimbolonstory.utils

import com.jonrysimbolonstory.model.StoryModel

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryModel(
                "created $i",
                "des $i",
                i.toString(),
                0.0,
                0.1,
                "name $i",
                "photo $i"
            )
            items.add(quote)
        }
        return items
    }
}