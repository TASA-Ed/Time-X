package top.tasaed.timex

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.about_close
import timex.shared.generated.resources.about_message
import timex.shared.generated.resources.about_title
import timex.shared.generated.resources.time_copied

/** 居中的当前系统时间，使用等宽字体，保证数字宽度一致。 */
@Composable
fun TimeScreen(
    modifier: Modifier = Modifier,
    timeTextSizeSp: Float = 57f,
    timeTextFormat: TimeTextFormat = TimeTextFormat.HoursMinutesSeconds,
    customTimeFormat: String = DefaultCustomTimeFormat,
    onTimeTextClick: () -> Unit = {},
) {
    var currentInstant by remember { mutableStateOf(Clock.System.now()) }
    val monoFontFamily = rememberSourceFontFamily()
    val displayTime by remember(timeTextFormat, customTimeFormat) {
        derivedStateOf { formatTimeText(currentInstant, timeTextFormat, customTimeFormat) }
    }
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val copiedMessage = stringResource(Res.string.time_copied)
    val copyCurrentTime: () -> Unit = {
        coroutineScope.launch {
            clipboard.setClipEntry(createPlainTextClipEntry(displayTime))
            snackbarHostState.showSnackbar(copiedMessage)
        }
    }
    val latestCopyCurrentTime = rememberUpdatedState(copyCurrentTime)

    LaunchedEffect(timeTextFormat) {
        while (true) {
            val now = Clock.System.now()
            currentInstant = now

            if (timeTextFormat == TimeTextFormat.UnixMilliseconds) {
                delay(updateIntervalMillis(timeTextFormat))
            } else {
                // Recalculate after every update so the next update stays aligned to a whole second.
                val millisUntilNextSecond = 1_000L - now.nanosecondsOfSecond / 1_000_000L
                delay(millisUntilNextSecond)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = displayTime,
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                latestCopyCurrentTime.value()
                                event.changes.forEach {
                                     it.consume() }
                            }
                        }
                    }
                }
                .combinedClickable(
                    onClick = onTimeTextClick,
                    onLongClick = copyCurrentTime,
                ),
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = monoFontFamily,
                fontSize = timeTextSizeSp.sp,
            ),
            textAlign = TextAlign.Center,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        )
    }
}

@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text(stringResource(Res.string.about_title)) },
        text = { Text(stringResource(Res.string.about_message)) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.about_close))
            }
        },
    )
}

