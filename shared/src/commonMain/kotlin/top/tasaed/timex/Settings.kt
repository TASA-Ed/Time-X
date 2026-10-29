package top.tasaed.timex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.settings_theme_black
import timex.shared.generated.resources.settings_theme_gray
import timex.shared.generated.resources.settings_theme_light_gray
import timex.shared.generated.resources.settings_theme_system
import timex.shared.generated.resources.settings_theme_title
import timex.shared.generated.resources.settings_theme_white
import timex.shared.generated.resources.settings_time_size_custom
import timex.shared.generated.resources.settings_time_size_slider
import timex.shared.generated.resources.settings_time_size_title
import timex.shared.generated.resources.settings_time_size_suffix
import timex.shared.generated.resources.settings_time_size_invalid

private const val TimeTextSizeSliderMin = 1f
private const val TimeTextSizeSliderMax = 400f

private enum class TimeTextSizeMode {
    Slider,
    Custom,
}

@Composable
fun SettingsContent(
    theme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    timeTextSizeSp: Float,
    onTimeTextSizeChanged: (Float) -> Unit,
) {
    var timeTextSizeMode by remember {
        mutableStateOf(
            if (timeTextSizeSp in TimeTextSizeSliderMin..TimeTextSizeSliderMax) {
                TimeTextSizeMode.Slider
            } else {
                TimeTextSizeMode.Custom
            },
        )
    }
    var customText by remember { mutableStateOf(formatTimeTextSize(timeTextSizeSp)) }
    var customError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Text(
            text = stringResource(Res.string.settings_theme_title),
            style = MaterialTheme.typography.titleLarge,
        )
        ThemeOption(AppTheme.System, theme, stringResource(Res.string.settings_theme_system), onThemeSelected)
        ThemeOption(AppTheme.White, theme, stringResource(Res.string.settings_theme_white), onThemeSelected)
        ThemeOption(AppTheme.Black, theme, stringResource(Res.string.settings_theme_black), onThemeSelected)
        ThemeOption(AppTheme.Gray, theme, stringResource(Res.string.settings_theme_gray), onThemeSelected)
        ThemeOption(AppTheme.LightGray, theme, stringResource(Res.string.settings_theme_light_gray), onThemeSelected)

        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.settings_time_size_title),
            style = MaterialTheme.typography.titleLarge,
        )
        TimeTextSizeModeOption(
            mode = TimeTextSizeMode.Slider,
            selected = timeTextSizeMode,
            label = stringResource(Res.string.settings_time_size_slider),
            onSelected = {
                customText = formatTimeTextSize(timeTextSizeSp)
                customError = false
                onTimeTextSizeChanged(timeTextSizeSp.coerceIn(TimeTextSizeSliderMin, TimeTextSizeSliderMax))
                timeTextSizeMode = it
            },
        )
        if (timeTextSizeMode == TimeTextSizeMode.Slider) {
            Slider(
                value = timeTextSizeSp.coerceIn(TimeTextSizeSliderMin, TimeTextSizeSliderMax),
                onValueChange = { onTimeTextSizeChanged(it.roundToInt().toFloat()) },
                valueRange = TimeTextSizeSliderMin..TimeTextSizeSliderMax,
                steps = 398,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${timeTextSizeSp.coerceIn(TimeTextSizeSliderMin, TimeTextSizeSliderMax).roundToInt()} ${stringResource(Res.string.settings_time_size_suffix)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        TimeTextSizeModeOption(
            mode = TimeTextSizeMode.Custom,
            selected = timeTextSizeMode,
            label = stringResource(Res.string.settings_time_size_custom),
            onSelected = {
                customText = formatTimeTextSize(timeTextSizeSp)
                customError = false
                timeTextSizeMode = it
            },
        )
        if (timeTextSizeMode == TimeTextSizeMode.Custom) {
            OutlinedTextField(
                value = customText,
                onValueChange = { value ->
                    customText = value
                    val parsed = value.toFloatOrNull()
                    if (parsed != null && parsed > 0f && parsed.isFinite()) {
                        customError = false
                        onTimeTextSizeChanged(parsed)
                    } else {
                        customError = true
                    }
                },
                label = { Text(stringResource(Res.string.settings_time_size_suffix)) },
                singleLine = true,
                isError = customError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = if (customError) {
                    { Text(stringResource(Res.string.settings_time_size_invalid)) }
                } else {
                    null
                },
            )
        }
    }
}

private fun formatTimeTextSize(value: Float): String =
    if (value == value.roundToInt().toFloat()) value.roundToInt().toString() else value.toString()

@Composable
private fun TimeTextSizeModeOption(
    mode: TimeTextSizeMode,
    selected: TimeTextSizeMode,
    label: String,
    onSelected: (TimeTextSizeMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(mode) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = mode == selected,
            onClick = { onSelected(mode) },
            modifier = Modifier.size(48.dp),
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun ThemeOption(
    option: AppTheme,
    selected: AppTheme,
    label: String,
    onSelected: (AppTheme) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(option) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = option == selected,
            onClick = { onSelected(option) },
            modifier = Modifier.size(48.dp),
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
expect fun SettingsDialog(
    theme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    timeTextSizeSp: Float,
    onTimeTextSizeChanged: (Float) -> Unit,
    onDismissRequest: () -> Unit,
)
