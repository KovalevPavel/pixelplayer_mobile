package kovp.pixelplayer.feature_albums.ui

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_main_flow.LocalMainScope
import kovp.pixelplayer.feature_albums.di.AlbumsScope
import kovp.pixelplayer.feature_albums.di.albumsModule
import kovp.pixelplayer.feature_albums.presentation.AlbumsAction
import kovp.pixelplayer.feature_albums.presentation.AlbumsState
import kovp.pixelplayer.feature_albums.presentation.AlbumsViewModel
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.getKoin

@Composable
fun AlbumsScaffoldWrapper(
    modifier: Modifier = Modifier,
) {
    val koin = getKoin()
    val mainScope = LocalMainScope.current
    val scope = remember {
        koin.loadModules(listOf(albumsModule))
        koin.getOrCreateScope<AlbumsScope>(AlbumsScope.toString())
    }

    scope.linkTo(mainScope)

    val viewModel: AlbumsViewModel = remember { scope.get() }

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
    state: AlbumsState,
    handleAction: (AlbumsAction) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "Albums") })
        },
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            targetState = state,
        ) { st ->
            when (st) {
                is AlbumsState.Error -> {
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
                                onClick = { handleAction(AlbumsAction.OnErrorActionClick) },
                            ) {
                                Text(text = st.action)
                            }
                        }
                    }
                }

                is AlbumsState.List -> {
                    AlbumsList(state = st, handleAction = handleAction)
                }

                is AlbumsState.Loading -> {
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
}

@AppPreview
@Composable
private fun ArtistsScaffoldPreview(
    @PreviewParameter(ArtistStateProvider::class) viewState: AlbumsState,
) {
    AppTheme {
        ArtistsScaffold(
            state = viewState,
            handleAction = {},
        )
    }
}

private class ArtistStateProvider : PreviewParameterProvider<AlbumsState> {
    override val values: Sequence<AlbumsState> = sequenceOf(
        AlbumsState.Loading,
        AlbumsState.Error(
            message = "Unexpected error",
            action = "Retry",
        ),
    )
}
