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
    viewState: PlayerVs,
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
            title = viewState.trackTitle,
            album = viewState.album,
            isExpanded = isExpanded,
            fraction = viewState.fraction,
            onSeek = {}
        )

        Controls(
            isPlaying = viewState.isPlaying,
            onPlayerAction = onPlayerAction,
        )
    }
}

@Composable
private fun ColumnScope.Controls(
    isPlaying: Boolean,
    onPlayerAction: (PlayerAction) -> Unit,
) {
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        IconButton(
            onClick = { onPlayerAction(PlayerAction.Previous) },
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        IconButton(
            onClick = {
                val action = if (isPlaying) PlayerAction.Pause else PlayerAction.Play
                onPlayerAction(action)
            },
        ) {
            val icon = if (isPlaying) {
                Icons.Default.Pause
            } else {
                Icons.Default.PlayCircle
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = { onPlayerAction(PlayerAction.Next) },
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@AppPreview
@Composable
private fun PlayerComposablePreview(
    @PreviewParameter(PlayerVsProvider::class) viewState: PlayerVs,
) {
    var isExpanded by remember { mutableStateOf(false) }
    PlayerComposable(
        modifier = Modifier.clickable { isExpanded = !isExpanded },
        viewState = viewState,
        isExpanded = isExpanded,
        onPlayerAction = {},
    )
}

private class PlayerVsProvider : PreviewParameterProvider<PlayerVs> {
    override val values: Sequence<PlayerVs> = sequenceOf(
        PlayerVs(
            isPlaying = false,
            trackTitle = "Track title",
            album = "Album",
            totalTime = "42:42",
            currentTime = "00:42",
            fraction = .2f,
        ),
        PlayerVs(
            isPlaying = true,
            trackTitle = "Track title ".repeat(10).trim(),
            album = "Album ".repeat(10).trim(),
            totalTime = "42:42",
            currentTime = "00:42",
            fraction = .7f,
        ),
    )
}
