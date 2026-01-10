package kovp.pixelplayer.feature_artists.ui

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
import kovp.pixelplayer.feature_artists.di.ArtistsScope
import kovp.pixelplayer.feature_artists.di.artistsModule
import kovp.pixelplayer.feature_artists.presentation.ArtistsAction
import kovp.pixelplayer.feature_artists.presentation.ArtistsState
import kovp.pixelplayer.feature_artists.presentation.ArtistsViewModel
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.getKoin

@Composable
fun ArtistsScaffoldWrapper(
    modifier: Modifier = Modifier,
) {
    val koin = getKoin()
    val mainScope = LocalMainScope.current
    val scope = remember {
        koin.loadModules(listOf(artistsModule))
        koin.getOrCreateScope<ArtistsScope>(ArtistsScope.toString())
    }

    scope.linkTo(mainScope)

    val viewModel: ArtistsViewModel = remember { scope.get() }

    ArtistsScaffold(
        modifier = modifier,
        state = viewModel.state,
        handleAction = viewModel::handleAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistsScaffold(
    modifier: Modifier = Modifier,
    state: ArtistsState,
    handleAction: (ArtistsAction) -> Unit,
) {
    AnimatedContent(
        modifier = modifier.fillMaxSize(),
        targetState = state,
    ) { st ->
        when (st) {
            is ArtistsState.Error -> {
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
                            onClick = { handleAction(ArtistsAction.OnErrorActionClick) },
                        ) {
                            Text(text = st.action)
                        }
                    }
                }
            }

            is ArtistsState.List -> {
                ArtistsList(state = st, handleAction = handleAction)
            }

            is ArtistsState.Loading -> {
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
private fun ArtistsScaffoldPreview(
    @PreviewParameter(ArtistStateProvider::class) viewState: ArtistsState,
) {
    AppTheme {
        ArtistsScaffold(
            state = viewState,
            handleAction = {},
        )
    }
}

private class ArtistStateProvider : PreviewParameterProvider<ArtistsState> {
    override val values: Sequence<ArtistsState> = sequenceOf(
        ArtistsState.Loading,
        ArtistsState.Error(
            message = "Unexpected error",
            action = "Retry",
        ),
    )
}
