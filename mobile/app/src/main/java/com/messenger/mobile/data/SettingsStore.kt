package com.messenger.mobile.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { System, Light, Dark }

data class AppSettings(
    val gridLayout: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.System,
    val dynamicColor: Boolean = false
)

class SettingsStore(private val context: Context) {
    private val gridKey = booleanPreferencesKey("grid")
    private val themeKey = stringPreferencesKey("theme")
    private val dynamicKey = booleanPreferencesKey("dynamic")

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { p ->
        AppSettings(
            gridLayout = p[gridKey] ?: true,
            themeMode = p[themeKey]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.System,
            dynamicColor = p[dynamicKey] ?: false
        )
    }

    suspend fun setGrid(value: Boolean) { context.settingsDataStore.edit { it[gridKey] = value } }
    suspend fun setTheme(mode: ThemeMode) { context.settingsDataStore.edit { it[themeKey] = mode.name } }
    suspend fun setDynamic(value: Boolean) { context.settingsDataStore.edit { it[dynamicKey] = value } }
}