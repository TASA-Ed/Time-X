package top.tasaed.timex

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
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
  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
  ) {
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
  }
}
