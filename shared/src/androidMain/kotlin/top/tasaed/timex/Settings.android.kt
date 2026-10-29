package top.tasaed.timex

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun SettingsDialog(
    theme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    timeTextSizeSp: Float,
    onTimeTextSizeChanged: (Float) -> Unit,
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
            timeTextSizeSp = timeTextSizeSp,
            onTimeTextSizeChanged = onTimeTextSizeChanged,
        )
    }
}
