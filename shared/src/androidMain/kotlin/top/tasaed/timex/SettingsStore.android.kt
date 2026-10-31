package top.tasaed.timex

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

private var settingsStore: SettingsStore? = null

@Synchronized
internal fun getSettingsStore(context: Context): SettingsStore =
  settingsStore
    ?: SettingsStore(context.applicationContext.settingsDataStore).also {
      settingsStore = it
    }
