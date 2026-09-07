package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "digest_settings")

class PreferencesManager internal constructor(
    private val dataStore: DataStore<Preferences>
) {

    constructor(context: Context) : this(context.dataStore)

    companion object {
        val ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
        val NOTIFICATION_PERMISSION_REQUESTED = booleanPreferencesKey("notification_permission_requested")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
        val DATA_SAVER = booleanPreferencesKey("data_saver")
        val TOPIC_PREFERENCES = stringSetPreferencesKey("topic_preferences")
        val LANGUAGE = stringPreferencesKey("language")
        val THEME_PROFILE = stringPreferencesKey("theme_profile")
    }

    val onboardingSeenFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_SEEN] ?: false
    }

    val notificationPermissionRequestedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_PERMISSION_REQUESTED] ?: false
    }

    val darkModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }

    val themeProfileFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME_PROFILE] ?: if (preferences[DARK_MODE] == true) "Dark" else "Light"
    }

    val notificationTimeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_TIME] ?: "08:00"
    }

    val dataSaverFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DATA_SAVER] ?: false
    }

    val topicPreferencesFlow: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[TOPIC_PREFERENCES] ?: emptySet()
    }

    val languageFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "English"
    }

    suspend fun setOnboardingSeen(seen: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_SEEN] = seen
        }
    }

    suspend fun setNotificationPermissionRequested(requested: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_REQUESTED] = requested
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
            preferences[THEME_PROFILE] = if (enabled) "Dark" else "Light"
        }
    }

    suspend fun setThemeProfile(profile: String) {
        dataStore.edit { preferences ->
            preferences[THEME_PROFILE] = profile
            preferences[DARK_MODE] = (profile == "Dark" || profile == "Charcoal")
        }
    }

    suspend fun setNotificationTime(time: String) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME] = time
        }
    }

    suspend fun setDataSaver(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATA_SAVER] = enabled
        }
    }

    suspend fun setTopicPreferences(topics: Set<String>) {
        dataStore.edit { preferences ->
            preferences[TOPIC_PREFERENCES] = topics
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }
}
