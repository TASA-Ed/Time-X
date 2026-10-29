package top.tasaed.timex

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

/** The caller owns one store per application directory. */
fun createDesktopSettingsStore(directory: File): SettingsStore = SettingsStore(
    PreferenceDataStoreFactory.create(
        produceFile = { File(directory, "tasaed_timex_settings.preferences_pb") },
    ),
)
