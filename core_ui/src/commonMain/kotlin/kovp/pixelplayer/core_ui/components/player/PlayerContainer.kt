package kovp.pixelplayer.core_ui.components.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.AppTypography

private const val TRACK_DATA_ID = "TRACK_DATA_ID"
private const val SLIDER_ID = "SLIDER_ID"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun PlayerCollapsingContainer(
    modifier: Modifier,
    title: String,
    album: String,
    isExpanded: Boolean,
    fraction: Float,
    onSeek: (Float) -> Unit,
) {
    SharedTransitionScope { m ->
        AnimatedContent(
            modifier = m.then(modifier),
            targetState = isExpanded,
            label = "player_anim",
        ) { isE ->
            if (isE) {
                ExpandedState(
                    modifier = Modifier.fillMaxWidth(),
                    trackTitle = title,
                    album = album,
                    fraction = fraction,
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    onSeek = onSeek,
                )
            } else {
                CollapsedState(
                    modifier = Modifier.fillMaxWidth(),
                    trackTitle = title,
                    album = album,
                    fraction = fraction,
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    onSeek = onSeek,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ExpandedState(
    modifier: Modifier,
    trackTitle: String,
    album: String,
    fraction: Float,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSeek: (Float) -> Unit,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TrackData(
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = TRACK_DATA_ID),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                    .fillMaxWidth(),
                trackTitle = trackTitle,
                album = album,
            )

            Slider(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SLIDER_ID),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(8.dp)
                ,
                fraction = fraction,
                onSeek = onSeek,
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CollapsedState(
    modifier: Modifier,
    trackTitle: String,
    album: String,
    fraction: Float,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSeek: (Float) -> Unit,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TrackData(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = TRACK_DATA_ID),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .weight(1f),
                trackTitle = trackTitle,
                album = album,
            )

            Slider(
                modifier = Modifier.padding(vertical = 8.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = SLIDER_ID),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .weight(1f),
                fraction = fraction,
                onSeek = onSeek,
            )
        }
    }
}

@Composable
private fun TrackData(
    modifier: Modifier,
    trackTitle: String,
    album: String,
) {
    Column(
        modifier = modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = trackTitle,
            style = AppTypography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = album,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun Slider(
    modifier: Modifier,
    fraction: Float,
    onSeek: (Float) -> Unit,
) {
    var maxWidth by remember { mutableIntStateOf(0) }
    var currentFraction by remember(fraction) { mutableStateOf(fraction) }

    Box(
        modifier = modifier
            .onSizeChanged { (width, _) -> maxWidth = width }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val e = awaitPointerEvent()
                        val lastChange = e.changes.lastOrNull()?.position?.x ?: 0f

                        when (e.type) {
                            PointerEventType.Move -> {
                                currentFraction = runCatching { lastChange / maxWidth }
                                    .getOrNull()
                                    ?: 0f
                            }

                            PointerEventType.Release -> {
                                currentFraction = runCatching { lastChange / maxWidth }
                                    .getOrNull()
                                    ?: 0f
                                onSeek(currentFraction)
                            }
                        }
                    }
                }
            }
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape,
            )
            .wrapContentHeight(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(fraction = currentFraction)
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ),
        )
    }
}

@AppPreview
@Composable
private fun PlayerCollapsingContainerPreview() {
    var isExpanded by remember { mutableStateOf(false) }
    AppTheme {
        PlayerCollapsingContainer(
            modifier = Modifier.clickable { isExpanded = !isExpanded },
            title = "Song title".repeat(10),
            album = "Album title".repeat(10),
            isExpanded = isExpanded,
            fraction = .4f,
            onSeek = {},
        )
    }
}
