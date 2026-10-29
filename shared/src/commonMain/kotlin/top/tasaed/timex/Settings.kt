package top.tasaed.timex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.settings_theme_black
import timex.shared.generated.resources.settings_theme_gray
import timex.shared.generated.resources.settings_theme_light_gray
import timex.shared.generated.resources.settings_theme_system
import timex.shared.generated.resources.settings_theme_title
import timex.shared.generated.resources.settings_theme_white

@Composable
fun SettingsContent(
    theme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
    onDismissRequest: () -> Unit,
)
