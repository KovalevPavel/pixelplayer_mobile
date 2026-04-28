package kov_p.pixelplayer.feature_artists.ui

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kov_p.pixelplayer.core_design.AppPreview
import kov_p.pixelplayer.core_design.AppTheme
import kov_p.pixelplayer.core_design.pixelColors
import kov_p.pixelplayer.core_design.pixelTypography
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.asString
import kov_p.pixelplayer.core_main_flow.LocalMainScope
import kov_p.pixelplayer.core_ui.CollectWithLifecycle
import kov_p.pixelplayer.feature_artists.di.ArtistsScope
import kov_p.pixelplayer.feature_artists.di.artistsModule
import kov_p.pixelplayer.feature_artists.presentation.ArtistsAction
import kov_p.pixelplayer.feature_artists.presentation.ArtistsEvent
import kov_p.pixelplayer.feature_artists.presentation.ArtistsState
import kov_p.pixelplayer.feature_artists.presentation.ArtistsViewModel
import org.koin.compose.getKoin

@Composable
fun ArtistsScaffoldWrapper(
    modifier: Modifier = Modifier,
    onArtistClick: (artistId: String) -> Unit,
) {
    val koin = getKoin()
    val mainScope = LocalMainScope.current
    val scope = remember {
        koin.loadModules(listOf(artistsModule))
        koin.getOrCreateScope<ArtistsScope>(ArtistsScope.toString())
    }

    scope.linkTo(mainScope)

    val viewModel: ArtistsViewModel = remember { scope.get() }

    viewModel.artistsEvents.CollectWithLifecycle { event ->
        when (event) {
            is ArtistsEvent.NavigateToArtist -> {
                onArtistClick(event.artistId)
            }
        }
    }

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
        contentKey = { it::class },
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
                            text = st.message.asString(),
                            textAlign = TextAlign.Center,
                            style = pixelTypography.bodyMedium,
                            color = pixelColors.onBackground,
                        )
                        OutlinedButton(
                            onClick = { handleAction(ArtistsAction.OnErrorActionClick) },
                        ) {
                            Text(text = st.action.asString())
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
            message = UiText.Dynamic("Unexpected error"),
            action = UiText.Dynamic("Retry"),
        ),
    )
}
