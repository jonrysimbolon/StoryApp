package com.jonrysimbolonstory.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jonrysimbolonstory.model.StoryModel

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<StoryModel>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getAllStory(): PagingSource<Int, StoryModel>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}