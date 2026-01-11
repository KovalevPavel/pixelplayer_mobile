package kovp.pixelplayer.feature_albums.list.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.feature_albums.di.AlbumsScope
import kovp.pixelplayer.feature_albums.di.albumsModule
import kovp.pixelplayer.feature_albums.list.AlbumsAction
import kovp.pixelplayer.feature_albums.list.AlbumsEvent
import kovp.pixelplayer.feature_albums.list.AlbumsState
import kovp.pixelplayer.feature_albums.list.AlbumsViewModel
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.getKoin

@Composable
fun AlbumsScaffoldWrapper(
    modifier: Modifier = Modifier,
    navigateToDetail: (String) -> Unit,
) {
    val koin = getKoin()
    val mainScope = LocalMainScope.current
    val scope = remember {
        koin.loadModules(listOf(albumsModule))
        koin.getOrCreateScope<AlbumsScope>(AlbumsScope.toString())
    }

    scope.linkTo(mainScope)

    val viewModel: AlbumsViewModel = remember { scope.get() }

    viewModel.eventsFlow.CollectWithLifecycle { event ->
        when (event) {
            is AlbumsEvent.NavigateToAlbum -> navigateToDetail(event.albumId)
        }
    }

    AlbumsScaffold(
        modifier = modifier,
        state = viewModel.state,
        handleAction = viewModel::handleAction,
    )
}

@Composable
private fun AlbumsScaffold(
    modifier: Modifier = Modifier,
    state: AlbumsState,
    handleAction: (AlbumsAction) -> Unit,
) {
    AnimatedContent(
        modifier = modifier.fillMaxSize(),
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

@AppPreview
@Composable
private fun AlbumsScaffoldPreview(
    @PreviewParameter(ArtistStateProvider::class) viewState: AlbumsState,
) {
    AppTheme {
        AlbumsScaffold(
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
