package top.tasaed.timex

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

data class AppSettings(
    val theme: AppTheme = AppTheme.White,
    val timeTextFormat: TimeTextFormat = TimeTextFormat.HoursMinutesSeconds,
    val customTimeFormat: String = DefaultCustomTimeFormat,
    val timeTextSizeSp: Float = 57f,
)

class SettingsStore internal constructor(private val dataStore: DataStore<Preferences>) {
    // Writes outlive settings dialogs and Android activity recreation.
    private val writeScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val settings = dataStore.data.map { preferences ->
        AppSettings(
            theme = AppTheme.entries.firstOrNull { it.name == preferences[ThemeKey] } ?: AppTheme.White,
            timeTextFormat = TimeTextFormat.entries.firstOrNull { it.name == preferences[TimeFormatKey] }
                ?: TimeTextFormat.HoursMinutesSeconds,
            customTimeFormat = preferences[CustomTimeFormatKey] ?: DefaultCustomTimeFormat,
            timeTextSizeSp = preferences[TimeTextSizeKey] ?: 57f,
        )
    }

    fun setTheme(theme: AppTheme) = writeScope.launch(start = CoroutineStart.UNDISPATCHED) {
        dataStore.edit { it[ThemeKey] = theme.name }
    }

    fun setTimeTextFormat(format: TimeTextFormat) = writeScope.launch(start = CoroutineStart.UNDISPATCHED) {
        dataStore.edit { it[TimeFormatKey] = format.name }
    }

    fun setCustomTimeFormat(pattern: String) = writeScope.launch(start = CoroutineStart.UNDISPATCHED) {
        dataStore.edit { it[CustomTimeFormatKey] = pattern }
    }

    fun setTimeTextSize(sizeSp: Float) = writeScope.launch(start = CoroutineStart.UNDISPATCHED) {
        dataStore.edit { it[TimeTextSizeKey] = sizeSp }
    }

    /** Call after the desktop UI stops submitting changes, before terminating the process. */
    suspend fun awaitPendingWrites() {
        writeScope.coroutineContext.job.children.toList().joinAll()
    }

    private companion object {
        val ThemeKey = stringPreferencesKey("theme")
        val TimeFormatKey = stringPreferencesKey("time_format")
        val CustomTimeFormatKey = stringPreferencesKey("custom_time_format")
        val TimeTextSizeKey = floatPreferencesKey("time_text_size_sp")
    }
}
