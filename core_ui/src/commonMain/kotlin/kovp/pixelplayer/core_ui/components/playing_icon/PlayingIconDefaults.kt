package kovp.pixelplayer.core_ui.components.playing_icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object PlayingIconDefaults {
    const val BARS_COUNT = 3
    private const val DELAY_MULTIPLIER = 52

    val color: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    fun getDelay(index: Int): Int {
        return (index + 1) * DELAY_MULTIPLIER
    }
}
