package top.tasaed.timex

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun createPlainTextClipEntry(text: String): ClipEntry =
  ClipEntry(StringSelection(text))
