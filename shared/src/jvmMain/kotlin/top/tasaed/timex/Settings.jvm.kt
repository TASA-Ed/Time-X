package top.tasaed.timex

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun SettingsDialog(
  theme: AppTheme,
  onThemeSelected: (AppTheme) -> Unit,
  timeTextFormat: TimeTextFormat,
  customTimeFormat: String,
  onTimeTextFormatChanged: (TimeTextFormat) -> Unit,
  onCustomTimeFormatChanged: (String) -> Unit,
  timeTextSizeSp: Float,
  onTimeTextSizeChanged: (Float) -> Unit,
  timeSourceMode: TimeSourceMode,
  onTimeSourceModeChanged: (TimeSourceMode) -> Unit,
  ntpServer: String,
  onNtpServerChanged: (String) -> Unit,
  onDismissRequest: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
    textContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
    modifier = Modifier.fillMaxWidth(0.7f).padding(vertical = 54.dp),
    properties =
      DialogProperties(
        usePlatformDefaultWidth = false,
      ),
    text = {
      SettingsContent(
        theme = theme,
        onThemeSelected = onThemeSelected,
        timeTextFormat = timeTextFormat,
        customTimeFormat = customTimeFormat,
        onTimeTextFormatChanged = onTimeTextFormatChanged,
        onCustomTimeFormatChanged = onCustomTimeFormatChanged,
        timeTextSizeSp = timeTextSizeSp,
        timeSourceMode = timeSourceMode,
        onTimeSourceModeChanged = onTimeSourceModeChanged,
        ntpServer = ntpServer,
        onNtpServerChanged = onNtpServerChanged,
        onTimeTextSizeChanged = onTimeTextSizeChanged,
      )
    },
    confirmButton = {},
  )
}
