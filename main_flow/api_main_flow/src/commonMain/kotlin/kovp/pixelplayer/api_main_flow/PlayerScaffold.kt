package kovp.pixelplayer.api_main_flow

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.components.player.PlayerComposable
import kovp.pixelplayer.core_ui.components.player.PlayerVs
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

private const val PLAYER_COLLAPSE_DELAY_MS = 3000L

@Composable
fun PlayerScaffold(
    modifier: Modifier = Modifier,
    viewState: PlayerVs,
    content: @Composable (Modifier) -> Unit,
) {
    var isExpanded: Boolean by remember { mutableStateOf(true) }
    val bottomPadding by animateDpAsState(if (isExpanded) 32.dp else 0.dp)
    val bottomRadius by animateDpAsState(if (isExpanded) 10.dp else 0.dp)

    val alpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else .4f
    )

    val pointerModifier = Modifier.pointerInput(Unit) {
        awaitEachGesture {
            awaitPointerEvent()
            isExpanded = false
        }
    }

    LaunchedEffect(isExpanded) {
        if (!isExpanded) {
            return@LaunchedEffect
        }

        delay(PLAYER_COLLAPSE_DELAY_MS)
        isExpanded = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        content(pointerModifier)

        if (viewState !is PlayerVs.Data) {
            return@Box
        }

        PlayerComposable(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alpha)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = bottomPadding)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomEnd = bottomRadius,
                        bottomStart = bottomRadius,
                    ),
                )
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            viewState = viewState,
            isExpanded = isExpanded,
            onPlayerAction = {},
        )
    }
}

@AppPreview
@Composable
private fun PlayerPreview(
    @PreviewParameter(PlayerVsProvider::class) viewState: PlayerVs,
) {
    AppTheme {
        PlayerScaffold(viewState = viewState) {
            val list = List(20) { i -> "element $i" }

            LazyColumn(
                modifier = Modifier
                    .then(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(64.dp),
                contentPadding = PaddingValues(bottom = 92.dp)
            ) {
                items(list) { e -> Text(text = e) }
            }
        }
    }
}

private class PlayerVsProvider : PreviewParameterProvider<PlayerVs> {
    override val values: Sequence<PlayerVs> = sequenceOf(
        PlayerVs.Data(
            trackId = "",
            isPlaying = false,
            trackTitle = "Track title",
            album = "Album",
            timeLine = PlayerVs.AudioTimeline(
                currentPositionMs = 4,
                durationMs = 10,
            ),
        ),
        PlayerVs.Data(
            trackId = "",
            isPlaying = true,
            trackTitle = "Track title ".repeat(10).trim(),
            album = "Album ".repeat(10).trim(),
            timeLine = PlayerVs.AudioTimeline(
                currentPositionMs = 4,
                durationMs = 10,
            ),
        ),
    )
}
