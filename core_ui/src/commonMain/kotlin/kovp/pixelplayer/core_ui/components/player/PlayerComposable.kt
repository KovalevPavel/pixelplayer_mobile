package kovp.pixelplayer.core_ui.components.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
fun PlayerComposable(
    modifier: Modifier = Modifier,
    viewState: PlayerVs.Data,
    isExpanded: Boolean,
    onPlayerAction: (PlayerAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        PlayerCollapsingContainer(
            modifier = Modifier.fillMaxWidth(),
            title = viewState.metaData.trackTitle.orEmpty(),
            album = viewState.metaData.album.orEmpty(),
            isExpanded = isExpanded,
            fraction = viewState.timeLine.fraction,
            onSeek = {
                onPlayerAction(
                    PlayerAction.Seek(fraction = it),
                )
            },
        )

        if (isExpanded) {
            Controls(
                isPlaying = viewState.isPlaying,
                hasNext = viewState.hasNext,
                onPlayerAction = onPlayerAction,
            )
        }
    }
}

@Composable
private fun ColumnScope.Controls(
    isPlaying: Boolean,
    hasNext: Boolean,
    onPlayerAction: (PlayerAction) -> Unit,
) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        IconButton(
            onClick = { onPlayerAction(PlayerAction.Previous) },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = null,
            )
        }

        IconButton(
            onClick = {
                val action = if (isPlaying) PlayerAction.Pause else PlayerAction.Resume
                onPlayerAction(action)
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            val icon = if (isPlaying) {
                Icons.Default.Pause
            } else {
                Icons.Default.PlayCircle
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        }

        IconButton(
            enabled = hasNext,
            onClick = { onPlayerAction(PlayerAction.Next) },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = null,
            )
        }
    }
}

@AppPreview
@Composable
private fun PlayerComposablePreview(
    @PreviewParameter(PlayerVsProvider::class) viewState: PlayerVs.Data,
) {
    var isExpanded by remember { mutableStateOf(false) }
    PlayerComposable(
        modifier = Modifier.clickable { isExpanded = !isExpanded },
        viewState = viewState,
        isExpanded = isExpanded,
        onPlayerAction = {},
    )
}

private class PlayerVsProvider : PreviewParameterProvider<PlayerVs.Data> {
    override val values: Sequence<PlayerVs.Data> = sequenceOf(
        PlayerVs.Data(
            trackId = "",
            isPlaying = false,
            metaData = PlayerVs.TrackMetaData(
                trackTitle = "Track title",
                album = "Album",
            ),
            timeLine = PlayerVs.AudioTimeline(
                currentPositionMs = 4,
                durationMs = 10,
            ),
            hasNext = false,
        ),
        PlayerVs.Data(
            trackId = "",
            isPlaying = true,
            metaData = PlayerVs.TrackMetaData(
                trackTitle = "Track title",
                album = "Album",
            ),
            timeLine = PlayerVs.AudioTimeline(
                currentPositionMs = 4,
                durationMs = 10,
            ),
            hasNext = true,
        ),
    )
}
