package kovp.pixelplayer.core_ui.components.playing_icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object PlayingIconDefaults {
    const val BARS_COUNT = 3

    val color: Color
        @Composable get() = MaterialTheme.colorScheme.primary
}
