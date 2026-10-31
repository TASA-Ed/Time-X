package top.tasaed.timex

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import org.jetbrains.compose.resources.stringResource
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.app_name
import timex.shared.generated.resources.menu_about
import timex.shared.generated.resources.menu_exit
import timex.shared.generated.resources.menu_file
import timex.shared.generated.resources.menu_help
import timex.shared.generated.resources.menu_open_source_licenses
import timex.shared.generated.resources.menu_settings

/** 本地化的应用名称，供窗口标题等平台代码使用。 */
@Composable fun appTitle(): String = stringResource(Res.string.app_name)

private enum class DesktopMenu {
  File,
  Help,
}

/** 使用 Compose 绘制的 Desktop 顶部菜单栏，避免依赖平台原生 MenuBar。 */
@Composable
fun FrameWindowScope.TimeXMenuBar(
  onSettings: () -> Unit,
  onAbout: () -> Unit,
  onOpenSourceLicenses: () -> Unit,
  onExit: () -> Unit,
) {
  var expandedMenu by remember { mutableStateOf<DesktopMenu?>(null) }
  val colors = MaterialTheme.colorScheme

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = colors.surface,
    tonalElevation = 2.dp,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MenuButton(
        label = stringResource(Res.string.menu_file),
        expanded = expandedMenu == DesktopMenu.File,
        onClick = {
          expandedMenu = if (expandedMenu == DesktopMenu.File) null else DesktopMenu.File
        },
      ) {
        DropdownMenuItem(
          text = {
            Text(
              stringResource(Res.string.menu_settings),
              color = MaterialTheme.colorScheme.onSurface,
            )
          },
          onClick = {
            expandedMenu = null
            onSettings()
          },
        )
        DropdownMenuItem(
          text = {
            Text(stringResource(Res.string.menu_exit), color = MaterialTheme.colorScheme.onSurface)
          },
          onClick = {
            expandedMenu = null
            onExit()
          },
        )
      }
      MenuButton(
        label = stringResource(Res.string.menu_help),
        expanded = expandedMenu == DesktopMenu.Help,
        onClick = {
          expandedMenu = if (expandedMenu == DesktopMenu.Help) null else DesktopMenu.Help
        },
      ) {
        DropdownMenuItem(
          text = {
            Text(stringResource(Res.string.menu_about), color = MaterialTheme.colorScheme.onSurface)
          },
          onClick = {
            expandedMenu = null
            onAbout()
          },
        )
        DropdownMenuItem(
          text = {
            Text(
              stringResource(Res.string.menu_open_source_licenses),
              color = MaterialTheme.colorScheme.onSurface,
            )
          },
          onClick = {
            expandedMenu = null
            onOpenSourceLicenses()
          },
        )
      }
    }
  }
}

@Composable
private fun MenuButton(
  label: String,
  expanded: Boolean,
  onClick: () -> Unit,
  menuContent: @Composable () -> Unit,
) {
  val colors = MaterialTheme.colorScheme
  val shape = RoundedCornerShape(8.dp)
  val backgroundColor = if (expanded) colors.secondaryContainer else Color.Transparent
  val contentColor = if (expanded) colors.onSecondaryContainer else colors.onSurface

  Box {
    Row(
      modifier =
        Modifier.clip(shape)
          .background(backgroundColor)
          .border(1.dp, if (expanded) colors.outline else Color.Transparent, shape)
          .clickable(onClick = onClick)
          .padding(horizontal = 12.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = onClick,
      modifier = Modifier.widthIn(min = 160.dp),
      containerColor = colors.surface,
    ) {
      menuContent()
    }
  }
}
