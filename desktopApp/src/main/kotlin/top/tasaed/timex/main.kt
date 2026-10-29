package top.tasaed.timex

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = appTitle(),
        icon = painterResource(Res.drawable.icon),
    ) {
        var showAbout by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }
        var theme by remember { mutableStateOf(AppTheme.White) }
        var timeTextFormat by remember { mutableStateOf(TimeTextFormat.HoursMinutesSeconds) }
        var customTimeFormat by remember { mutableStateOf(DefaultCustomTimeFormat) }
        var timeTextSizeSp by remember { mutableStateOf(57f) }
        var showTopBar by remember { mutableStateOf(true) }

        TimeXTheme(theme = theme) {
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
                        timeTextSizeSp = timeTextSizeSp,
                        timeTextFormat = timeTextFormat,
                        customTimeFormat = customTimeFormat,
                        onTimeTextClick = { showTopBar = !showTopBar }
                    )
                }
            }
            if (showSettings) {
                SettingsDialog(
                    theme = theme,
                    onThemeSelected = { theme = it },
                    timeTextFormat = timeTextFormat,
                    customTimeFormat = customTimeFormat,
                    onTimeTextFormatChanged = { timeTextFormat = it },
                    onCustomTimeFormatChanged = { customTimeFormat = it },
                    timeTextSizeSp = timeTextSizeSp,
                    onTimeTextSizeChanged = { timeTextSizeSp = it },
                    onDismissRequest = { showSettings = false },
                )
            }
            if (showAbout) {
                AboutDialog(onDismissRequest = { showAbout = false })
            }
        }
    }
}
