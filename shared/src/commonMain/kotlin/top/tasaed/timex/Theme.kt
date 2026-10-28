package top.tasaed.timex

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun TimeXTheme(content: @Composable () -> Unit) {
    val uiFontFamily = rememberSourceHanFontFamily()
    val typography = remember(uiFontFamily) { Typography(fontFamily = uiFontFamily) }
    MaterialTheme(typography = typography, content = content)
}
