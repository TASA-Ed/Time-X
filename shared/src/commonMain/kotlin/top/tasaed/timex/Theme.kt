package top.tasaed.timex

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

enum class AppTheme {
  System,
  White,
  Black,
  Gray,
  LightGray,
}

@Composable
fun TimeXTheme(
  theme: AppTheme = AppTheme.White,
  content: @Composable () -> Unit,
) {
  val uiFontFamily = rememberSourceHanFontFamily()
  val typography = remember(uiFontFamily) { Typography(fontFamily = uiFontFamily) }
  val systemDark = isSystemInDarkTheme()
  val dark =
    when (theme) {
      AppTheme.System -> systemDark
      AppTheme.Black -> true
      AppTheme.White,
      AppTheme.Gray,
      AppTheme.LightGray -> false
    }
  val colors =
    remember(theme, dark) {
      when (theme) {
        AppTheme.White -> lightColorScheme()
        AppTheme.Black -> darkColorScheme()
        AppTheme.Gray -> grayColorScheme()
        AppTheme.LightGray -> lightGrayColorScheme()
        AppTheme.System -> if (dark) darkColorScheme() else lightColorScheme()
      }
    }
  MaterialTheme(colorScheme = colors, typography = typography, content = content)
}

@Composable
fun TimeXSurface(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    content = content,
  )
}

private fun grayColorScheme() =
  darkColorScheme(
    background = Color(0xFF606060),
    surface = Color(0xFF686868),
    surfaceVariant = Color(0xFF7A7A7A),
  )

private fun lightGrayColorScheme() =
  lightColorScheme(
    background = Color(0xFFF1F1F1),
    surface = Color(0xFFF7F7F7),
    surfaceVariant = Color(0xFFE3E3E3),
  )
