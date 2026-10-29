package top.tasaed.timex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.about_close
import timex.shared.generated.resources.about_message
import timex.shared.generated.resources.about_title

/** 居中的当前系统时间，使用等宽字体，保证数字宽度一致。 */
@Composable
fun TimeScreen(modifier: Modifier = Modifier) {
    var currentInstant by remember { mutableStateOf(Clock.System.now()) }
    val timeZone = remember { TimeZone.currentSystemDefault() }
    val monoFontFamily = rememberSourceFontFamily()
    val displayTime by remember(timeZone) {
        derivedStateOf {
            val time = currentInstant.toLocalDateTime(timeZone).time
            "${time.hour.toString().padStart(2, '0')}:" +
                "${time.minute.toString().padStart(2, '0')}:" +
                time.second.toString().padStart(2, '0')
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Clock.System.now()
            currentInstant = now

            // Recalculate after every update so the next update stays aligned to a whole second.
            val millisUntilNextSecond = 1_000L - now.nanosecondsOfSecond / 1_000_000L
            delay(millisUntilNextSecond)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = displayTime,
            style = MaterialTheme.typography.displayLarge.copy(fontFamily = monoFontFamily),
            textAlign = TextAlign.Center,
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

