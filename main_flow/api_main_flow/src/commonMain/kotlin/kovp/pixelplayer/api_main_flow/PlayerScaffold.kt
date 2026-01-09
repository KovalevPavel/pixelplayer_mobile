package kovp.pixelplayer.api_main_flow

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.components.player.PlayerComposable
import kovp.pixelplayer.core_ui.components.player.PlayerVs
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScaffold(
    viewState: PlayerVs,
) {
    val bottomSheetState = rememberStandardBottomSheetState(
        skipHiddenState = true,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState,
    )

    val alpha by animateFloatAsState(
        targetValue = if (bottomSheetState.targetValue == SheetValue.Expanded) 1f else .4f
    )

    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetContent = {
            Box(
                modifier = Modifier
                    .alpha(alpha)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(size = 10.dp)
                    ),
            ) {
                PlayerComposable(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    viewState = viewState,
                    isExpanded = bottomSheetState.targetValue == SheetValue.Expanded,
                    onPlayerAction = {},
                )
            }
        },
        sheetShadowElevation = 0.dp,
        sheetDragHandle = {},
        sheetContainerColor = Color.Transparent,
        sheetShape = RectangleShape,
        sheetPeekHeight = 78.dp,
    ) {
        val list = List(20) { "element $it" }

        LazyColumn(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitPointerEvent()
                        scope.launch {
                            bottomSheetState.partialExpand()
                        }
                    }
                }
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            contentPadding = PaddingValues(bottom = 92.dp)
        ) {
            items(list) { e -> Text(text = e) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@AppPreview
@Composable
private fun PlayerPreview(
    @PreviewParameter(PlayerVsProvider::class) viewState: PlayerVs,
) {
    AppTheme {
        PlayerScaffold(viewState = viewState)
    }
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
