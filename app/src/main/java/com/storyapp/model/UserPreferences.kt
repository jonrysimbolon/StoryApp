package com.storyapp.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.storyapp.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val userId = stringPreferencesKey(BuildConfig.USERID)
    private val name = stringPreferencesKey(BuildConfig.NAME)
    private val token = stringPreferencesKey(BuildConfig.TOKEN)
    private val state = booleanPreferencesKey(BuildConfig.STATE)

    fun isLogin(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[state] ?: false
        }
    }

    fun getToken(): Flow<String>{
        return dataStore.data.map { preferences ->
            preferences[token] ?: ""
        }
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[userId] ?: "",
                preferences[name] ?: "",
                preferences[token] ?: "",
                preferences[state] ?: false,
            )
        }
    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[userId] = user.userId
            preferences[name] = user.name
            preferences[state] = user.isLogin
            preferences[token] = user.token
        }
    }

    suspend fun resetUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}