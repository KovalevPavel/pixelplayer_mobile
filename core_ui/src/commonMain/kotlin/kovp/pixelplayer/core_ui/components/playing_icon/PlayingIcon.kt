package kovp.pixelplayer.core_ui.components.playing_icon

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppTheme

@Composable
fun PlayingIcon(
    modifier: Modifier = Modifier,
    count: Int = PlayingIconDefaults.BARS_COUNT,
    color: Color = PlayingIconDefaults.color,
    isPlaying: Boolean,
) {
    Row(
        modifier = modifier.size(24.dp).padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        repeat(count) {
            Bar(
                delay = PlayingIconDefaults.getDelay(it),
                color = color,
                isPlaying = isPlaying,
            )
        }
    }
}

@Composable
private fun RowScope.Bar(
    delay: Int,
    color: Color,
    isPlaying: Boolean,
) {
    var flag by remember { mutableStateOf(false) }
    val value by animateFloatAsState(
        targetValue = run {
            when {
                isPlaying -> if (flag) 1f else 0f
                else -> .1f
            }
        },
        animationSpec = tween(
            delayMillis = delay,
        ),
        finishedListener = {
            flag = !flag
        },
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .background(color = color)
            .fillMaxHeight(value),
    )

    DisposableEffect(Unit) {
        flag = !flag
        onDispose { }
    }
}

@Preview
@Composable
private fun PlayingIconPreview(
    @PreviewParameter(IsPlayingProvider::class) isPlaying: Boolean,
) {
    AppTheme {
        PlayingIcon(isPlaying = isPlaying)
    }
}

private class IsPlayingProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(
        true,
        false,
    )
}
