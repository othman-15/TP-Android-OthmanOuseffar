package com.example.emtyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.emtyapp.data.Entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
    }


    suspend fun saveUserData(user: User) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_DATA_KEY] = Json.encodeToString(User.serializer(), user)
        }
    }


    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false
    }


    val userData: Flow<User?> = dataStore.data.map { preferences ->
        val userDataString = preferences[USER_DATA_KEY]
        if (userDataString != null) {
            try {
                Json.decodeFromString(User.serializer(), userDataString)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }


    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN_KEY)
            preferences.remove(USER_DATA_KEY)
        }
    }
}