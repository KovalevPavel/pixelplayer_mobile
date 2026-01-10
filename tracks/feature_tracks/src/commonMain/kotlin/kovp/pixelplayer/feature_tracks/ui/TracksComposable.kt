package kovp.pixelplayer.feature_tracks.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_main_flow.LocalMainScope
import kovp.pixelplayer.feature_tracks.di.TracksScope
import kovp.pixelplayer.feature_tracks.di.tracksModule
import kovp.pixelplayer.feature_tracks.presentation.TracksAction
import kovp.pixelplayer.feature_tracks.presentation.TracksState
import kovp.pixelplayer.feature_tracks.presentation.TracksViewModel
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.getKoin

@Composable
fun TracksScaffoldWrapper(
    modifier: Modifier = Modifier,
) {
    val koin = getKoin()
    val mainScope = LocalMainScope.current
    val scope = remember {
        koin.loadModules(listOf(tracksModule))
        koin.getOrCreateScope<TracksScope>(TracksScope.toString())
    }

    scope.linkTo(mainScope)

    val viewModel: TracksViewModel = remember { scope.get() }

    TracksScaffold(
        modifier = modifier,
        state = viewModel.state,
        handleAction = viewModel::handleAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TracksScaffold(
    modifier: Modifier = Modifier,
    state: TracksState,
    handleAction: (TracksAction) -> Unit,
) {
    AnimatedContent(
        modifier = modifier.fillMaxSize(),
        targetState = state,
    ) { st ->
        when (st) {
            is TracksState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = st.message,
                            textAlign = TextAlign.Center,
                        )
                        OutlinedButton(
                            onClick = { handleAction(TracksAction.OnErrorActionClick) },
                        ) {
                            Text(text = st.action)
                        }
                    }
                }
            }

            is TracksState.List -> {
                TracksList(state = st, handleAction = handleAction)
            }

            is TracksState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@AppPreview
@Composable
private fun TracksScaffoldPreview(
    @PreviewParameter(TracksStateProvider::class) viewState: TracksState,
) {
    AppTheme {
        TracksScaffold(
            state = viewState,
            handleAction = {},
        )
    }
}

private class TracksStateProvider : PreviewParameterProvider<TracksState> {
    override val values: Sequence<TracksState> = sequenceOf(
        TracksState.Loading,
        TracksState.Error(
            message = "Unexpected error",
            action = "Retry",
        ),
    )
}
