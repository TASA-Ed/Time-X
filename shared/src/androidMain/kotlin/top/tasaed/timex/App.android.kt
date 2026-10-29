package top.tasaed.timex

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    DisposableEffect(activity, isLandscape) {
        val window = activity?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        val previousBehavior = controller?.systemBarsBehavior
        if (isLandscape) {
            controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            controller?.show(WindowInsetsCompat.Type.statusBars())
        }
        onDispose {
            if (isLandscape) {
                controller?.show(WindowInsetsCompat.Type.statusBars())
                if (previousBehavior != null) {
                    controller.systemBarsBehavior = previousBehavior
                }
            }
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val settingsStore = remember(context.applicationContext) { getSettingsStore(context) }
    val storedSettings by settingsStore.settings.collectAsState(initial = null)
    val settings = storedSettings ?: return
    var showTopBar by remember { mutableStateOf(true) }

    TimeXTheme(theme = settings.theme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = if (isLandscape) {
                ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars)
            } else {
                ScaffoldDefaults.contentWindowInsets
            },
            topBar = {
                if (showTopBar) {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) },
                    windowInsets = if (isLandscape) {
                        TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars)
                    } else {
                        TopAppBarDefaults.windowInsets
                    },
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
                                    if (isLandscape) {
                                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                timeTextSizeSp = settings.timeTextSizeSp,
                timeTextFormat = settings.timeTextFormat,
                customTimeFormat = settings.customTimeFormat,
                onTimeTextClick = { showTopBar = !showTopBar }
            )
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

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}