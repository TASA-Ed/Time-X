package top.tasaed.timex

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.about_close
import timex.shared.generated.resources.about_creator_label
import timex.shared.generated.resources.about_creator_name
import timex.shared.generated.resources.about_github
import timex.shared.generated.resources.about_message
import timex.shared.generated.resources.about_title
import timex.shared.generated.resources.license_apache_2
import timex.shared.generated.resources.license_apache_2_components
import timex.shared.generated.resources.license_bsd_3_clause
import timex.shared.generated.resources.license_bsd_3_clause_components
import timex.shared.generated.resources.license_sil_1_1
import timex.shared.generated.resources.license_sil_1_1_components
import timex.shared.generated.resources.license_unicode_3
import timex.shared.generated.resources.license_unicode_3_components
import timex.shared.generated.resources.licenses_title
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
  val displayTime by
    remember(timeTextFormat, customTimeFormat) {
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
      modifier =
        Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
              while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                  latestCopyCurrentTime.value()
                  event.changes.forEach {
                    it.consume()
                  }
                }
              }
            }
          }
          .combinedClickable(
            onClick = onTimeTextClick,
            onLongClick = copyCurrentTime,
          ),
      style =
        MaterialTheme.typography.displayLarge.copy(
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
  val uriHandler = LocalUriHandler.current
  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurface,
    title = { Text(stringResource(Res.string.about_title)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(Res.string.about_message))
        Spacer(Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = stringResource(Res.string.about_creator_label),
            style = MaterialTheme.typography.labelLarge,
          )
          TextButton(onClick = { uriHandler.openUri("https://www.tasaed.top/") }) {
            Text(
              text = stringResource(Res.string.about_creator_name),
              style = MaterialTheme.typography.titleMedium,
            )
          }
        }
        Spacer(Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = stringResource(Res.string.about_github),
            style = MaterialTheme.typography.labelLarge,
          )
          TextButton(onClick = { uriHandler.openUri("https://github.com/TASA-Ed/Time-X") }) {
            Text(
              text = "TASA-Ed/Time-X",
              style = MaterialTheme.typography.titleMedium,
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismissRequest) {
        Text(stringResource(Res.string.about_close))
      }
    },
  )
}

@Composable
fun OpenSourceLicensesDialog(onDismissRequest: () -> Unit) {
  val uriHandler = LocalUriHandler.current
  val openLicensesPage =
    fun(url: String): () -> Unit {
      return { uriHandler.openUri("https://opensource.org/licenses/" + url) }
    }
  AlertDialog(
    onDismissRequest = onDismissRequest,
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurface,
    title = { Text(stringResource(Res.string.licenses_title)) },
    text = {
      Column(
        modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        LicenseEntry(
          name = stringResource(Res.string.license_apache_2),
          components = stringResource(Res.string.license_apache_2_components),
          onClick = openLicensesPage("apache-2.0"),
        )
        LicenseEntry(
          name = stringResource(Res.string.license_bsd_3_clause),
          components = stringResource(Res.string.license_bsd_3_clause_components),
          onClick = openLicensesPage("BSD-3-clause"),
        )
        LicenseEntry(
          name = stringResource(Res.string.license_unicode_3),
          components = stringResource(Res.string.license_unicode_3_components),
          onClick = openLicensesPage("Unicode-3.0"),
        )
        LicenseEntry(
          name = stringResource(Res.string.license_sil_1_1),
          components = stringResource(Res.string.license_sil_1_1_components),
          onClick = openLicensesPage("OFL-1.1"),
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismissRequest) {
        Text(stringResource(Res.string.about_close))
      }
    },
  )
}

@Composable
private fun LicenseEntry(name: String, components: String, onClick: () -> Unit) {
  Card(onClick = onClick) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(name, style = MaterialTheme.typography.titleSmall)
      Text(components, style = MaterialTheme.typography.bodyMedium)
    }
  }
}
