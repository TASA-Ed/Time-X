package top.tasaed.timex

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.app_name
import timex.shared.generated.resources.ic_more_vert
import timex.shared.generated.resources.menu
import timex.shared.generated.resources.menu_about
import timex.shared.generated.resources.menu_settings
import timex.shared.generated.resources.menu_landscape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val context = LocalContext.current
    val activity = context.findActivity()
    val configuration = LocalConfiguration.current
    var menuExpanded by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var theme by remember { mutableStateOf(AppTheme.White) }
    var timeTextFormat by remember { mutableStateOf(TimeTextFormat.HoursMinutesSeconds) }
    var customTimeFormat by remember { mutableStateOf(DefaultCustomTimeFormat) }
    var timeTextSizeSp by remember { mutableStateOf(57f) }
    var showTopBar by remember { mutableStateOf(true) }

    TimeXTheme(theme = theme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (showTopBar) {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_more_vert),
                                contentDescription = stringResource(Res.string.menu),
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_landscape)) },
                                onClick = {
                                    menuExpanded = false
                                    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                                    if (isLandscape) {
                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                    } else {
                                        // 当前是竖屏，切换为横屏
                                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                    }
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_settings)) },
                                onClick = {
                                    menuExpanded = false
                                    showSettings = true
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_about)) },
                                onClick = {
                                    menuExpanded = false
                                    showAbout = true
                                },
                            )
                        }
                    },
                )
                }
            },
        ) { innerPadding ->
            TimeScreen(
                modifier = Modifier.padding(innerPadding),
                timeTextSizeSp = timeTextSizeSp,
                timeTextFormat = timeTextFormat,
                customTimeFormat = customTimeFormat,
                onTimeTextClick = { showTopBar = !showTopBar }
            )
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

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}