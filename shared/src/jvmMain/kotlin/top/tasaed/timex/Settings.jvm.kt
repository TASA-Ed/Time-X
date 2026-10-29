package top.tasaed.timex

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

@Composable
actual fun SettingsDialog(
    theme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    timeTextSizeSp: Float,
    onTimeTextSizeChanged: (Float) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
        textContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
        text = {
            SettingsContent(
                theme = theme,
                onThemeSelected = onThemeSelected,
                timeTextSizeSp = timeTextSizeSp,
                onTimeTextSizeChanged = onTimeTextSizeChanged,
            )
        },
        confirmButton = {},
    )
}
