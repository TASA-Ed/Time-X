package top.tasaed.timex

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

internal actual fun createPlainTextClipEntry(text: String): ClipEntry =
    ClipEntry(ClipData.newPlainText("", text))
