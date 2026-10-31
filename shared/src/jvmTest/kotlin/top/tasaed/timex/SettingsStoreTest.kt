package top.tasaed.timex

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingsStoreTest {
  @Test
  fun rapidChangesKeepLatestValuesAcrossStoreRecreation() = runBlocking {
    val directory = Files.createTempDirectory("timex-settings-test").toFile()
    val file = directory.resolve("settings.preferences_pb")
    var storeJob = SupervisorJob()
    fun openStore() =
      SettingsStore(
        PreferenceDataStoreFactory.create(
          scope = CoroutineScope(Dispatchers.IO + storeJob),
          produceFile = { file },
        ),
      )

    try {
      val store = openStore()
      store.setTheme(AppTheme.Black)
      store.setTimeTextFormat(TimeTextFormat.Custom)
      store.setCustomTimeFormat("yyyy-MM-dd '时间' HH:mm:ss")
      repeat(100) { store.setTimeTextSize(it + 1f) }
      store.setTimeTextSize(512.5f)
      store.awaitPendingWrites()
      storeJob.cancelAndJoin()

      storeJob = SupervisorJob()
      val restored = openStore().settings.first()
      assertEquals(
        AppSettings(
          theme = AppTheme.Black,
          timeTextFormat = TimeTextFormat.Custom,
          customTimeFormat = "yyyy-MM-dd '时间' HH:mm:ss",
          timeTextSizeSp = 512.5f,
        ),
        restored,
      )
    } finally {
      storeJob.cancelAndJoin()
      directory.deleteRecursively()
    }
  }
}
