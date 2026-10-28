package top.tasaed.timex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import timex.shared.generated.resources.Res
import timex.shared.generated.resources.source_sans3_bold
import timex.shared.generated.resources.source_sans3_regular
import timex.shared.generated.resources.source_han_sans_sc_bold
import timex.shared.generated.resources.source_han_sans_sc_regular

/** Source Sans 用于时间等需要对齐的数字文本 */
@Composable
fun rememberSourceFontFamily(): FontFamily {
    val regular = Font(Res.font.source_sans3_bold, FontWeight.Normal, FontStyle.Normal)
    val bold = Font(
        Res.font.source_sans3_regular, FontWeight.Bold, FontStyle.Normal, variationSettings = FontVariation.Settings(
            FontVariation.Setting("tnum", 1.0f)
        )
    )
    return remember(regular, bold) { FontFamily(regular, bold) }
}

/** Source Han Sans SC 用于界面文字 */
@Composable
fun rememberSourceHanFontFamily(): FontFamily {
    val regular = Font(Res.font.source_han_sans_sc_regular, FontWeight.Normal, FontStyle.Normal)
    val bold = Font(Res.font.source_han_sans_sc_bold, FontWeight.Bold, FontStyle.Normal)
    return remember(regular, bold) { FontFamily(regular, bold) }
}
