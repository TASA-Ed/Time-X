package top.tasaed.timex

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.icon

private object DesktopApplication

private fun settingsDirectory(): File {
  val userHome = File(System.getProperty("user.home"))
  val osName = System.getProperty("os.name").lowercase(Locale.ROOT)

  val directory =
    when {
      osName.contains("win") -> {
        val appData =
          System.getenv("APPDATA")?.takeIf { it.isNotBlank() }
            ?: File(userHome, "AppData/Roaming").path
        File(appData, "TASA-Ed/Time-X")
      }
      osName.contains("mac") || osName.contains("darwin") ->
        File(userHome, "Library/Application Support/Time-X")
      else -> {
        val configHome =
          System.getenv("XDG_CONFIG_HOME")?.takeIf { it.isNotBlank() }
            ?: File(userHome, ".config").path
        File(configHome, "Time-X")
      }
    }

  directory.mkdirs()
  return directory
}

fun main() {
  val settingsStore = createDesktopSettingsStore(settingsDirectory())
  try {
    runApplication(settingsStore)
  } finally {
    runBlocking { settingsStore.awaitPendingWrites() }
  }
}

private fun runApplication(settingsStore: SettingsStore) =
  application(exitProcessOnExit = false) {
    Window(
      onCloseRequest = ::exitApplication,
      title = appTitle(),
      icon = painterResource(Res.drawable.icon),
    ) {
      var showAbout by remember { mutableStateOf(false) }
      var showLicenses by remember { mutableStateOf(false) }
      var showSettings by remember { mutableStateOf(false) }
      var syncRequest by remember { mutableStateOf(0) }
      var showTimeDifferenceRequest by remember { mutableStateOf(0) }
      val storedSettings by settingsStore.settings.collectAsState(initial = null)
      val settings = storedSettings ?: return@Window
      var showTopBar by remember { mutableStateOf(true) }

      TimeXTheme(theme = settings.theme) {
        TimeXSurface(Modifier.fillMaxSize()) {
          Column(Modifier.fillMaxSize()) {
            if (showTopBar) {
              TimeXMenuBar(
                onSettings = { showSettings = true },
                onSyncNow = { syncRequest++ },
                onShowTimeDifference = { showTimeDifferenceRequest++ },
                onAbout = { showAbout = true },
                onOpenSourceLicenses = { showLicenses = true },
                onExit = ::exitApplication,
                showNetworkTime = settings.timeSourceMode == TimeSourceMode.Network,
              )
            }
            TimeScreen(
              modifier = Modifier.weight(1f),
              timeTextSizeSp = settings.timeTextSizeSp,
              timeTextFormat = settings.timeTextFormat,
              customTimeFormat = settings.customTimeFormat,
              timeSourceMode = settings.timeSourceMode,
              ntpServer = settings.ntpServer,
              syncRequest = syncRequest,
              showTimeDifferenceRequest = showTimeDifferenceRequest,
              onTimeTextClick = { showTopBar = !showTopBar },
            )
          }
        }
        if (showSettings) {
          SettingsDialog(
            theme = settings.theme,
            onThemeSelected = { settingsStore.setTheme(it) },
            timeTextFormat = settings.timeTextFormat,
            customTimeFormat = settings.customTimeFormat,
            onTimeTextFormatChanged = { settingsStore.setTimeTextFormat(it) },
            onCustomTimeFormatChanged = { settingsStore.setCustomTimeFormat(it) },
            timeTextSizeSp = settings.timeTextSizeSp,
            onTimeTextSizeChanged = { settingsStore.setTimeTextSize(it) },
            timeSourceMode = settings.timeSourceMode,
            onTimeSourceModeChanged = { settingsStore.setTimeSourceMode(it) },
            ntpServer = settings.ntpServer,
            onNtpServerChanged = { settingsStore.setNtpServer(it) },
            onDismissRequest = { showSettings = false },
          )
        }
        if (showAbout) {
          AboutDialog(onDismissRequest = { showAbout = false })
        }
        if (showLicenses) {
          OpenSourceLicensesDialog(onDismissRequest = { showLicenses = false })
        }
      }
    }
  }
