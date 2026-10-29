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
        var timeTextSizeSp by remember { mutableStateOf(57f) }

        TimeXTheme(theme = theme) {
            TimeXSurface(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize()) {
                    TimeXMenuBar(
                        onSettings = { showSettings = true },
                        onAbout = { showAbout = true },
                        onExit = ::exitApplication,
                    )
                    TimeScreen(Modifier.weight(1f), timeTextSizeSp)
                }
            }
            if (showSettings) {
                SettingsDialog(
                    theme = theme,
                    onThemeSelected = { theme = it },
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
