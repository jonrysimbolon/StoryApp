package com.jonrysimbolonstory.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jonrysimbolonstory.local.dao.RemoteKeyDao
import com.jonrysimbolonstory.local.dao.StoryDao
import com.jonrysimbolonstory.model.RemoteKeys
import com.jonrysimbolonstory.model.StoryModel

@Database(
    entities = [StoryModel::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeyDao
}