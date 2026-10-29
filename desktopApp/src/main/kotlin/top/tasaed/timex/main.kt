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
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.icon

private object DesktopApplication

private fun settingsDirectory(): File {
    // jpackage exposes the native launcher path even when started from another working directory.
    val launcherPath = System.getProperty("jpackage.app-path")
    if (!launcherPath.isNullOrBlank()) return File(launcherPath).absoluteFile.parentFile

    // Gradle runs from classes; a standalone JAR stores its settings beside that JAR.
    val location = File(DesktopApplication::class.java.protectionDomain.codeSource.location.toURI())
    return if (location.isFile) location.parentFile else location
}

fun main() {
    val settingsStore = createDesktopSettingsStore(settingsDirectory())
    try {
        runApplication(settingsStore)
    } finally {
        runBlocking { settingsStore.awaitPendingWrites() }
    }
}

private fun runApplication(settingsStore: SettingsStore) = application(exitProcessOnExit = false) {
    Window(
        onCloseRequest = ::exitApplication,
        title = appTitle(),
        icon = painterResource(Res.drawable.icon),
    ) {
        var showAbout by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        val storedSettings by settingsStore.settings.collectAsState(initial = null)
        val settings = storedSettings ?: return@Window
        var showTopBar by remember { mutableStateOf(true) }

        TimeXTheme(theme = settings.theme) {
            TimeXSurface(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize()) {
                    if (showTopBar) {
                        TimeXMenuBar(
                            onSettings = { showSettings = true },
                            onAbout = { showAbout = true },
                            onExit = ::exitApplication,
                        )
                    }
                    TimeScreen(
                        modifier = Modifier.weight(1f),
                        timeTextSizeSp = settings.timeTextSizeSp,
                        timeTextFormat = settings.timeTextFormat,
                        customTimeFormat = settings.customTimeFormat,
                        onTimeTextClick = { showTopBar = !showTopBar }
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
                    onDismissRequest = { showSettings = false },
                )
            }
            if (showAbout) {
                AboutDialog(onDismissRequest = { showAbout = false })
            }
        }
    }
}
