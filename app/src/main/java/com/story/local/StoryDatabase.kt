package com.story.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.story.local.dao.RemoteKeyDao
import com.story.local.dao.StoryDao
import com.story.model.RemoteKeys
import com.story.model.StoryModel

@Database(
    entities = [StoryModel::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeyDao
}