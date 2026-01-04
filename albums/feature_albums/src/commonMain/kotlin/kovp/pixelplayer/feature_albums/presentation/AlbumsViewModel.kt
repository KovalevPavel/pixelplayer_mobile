package kovp.pixelplayer.feature_albums.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_albums.AlbumsRepository

internal class AlbumsViewModel(
    private val repository: AlbumsRepository,
) : ViewModel() {
    var state: AlbumsState by mutableStateOf(AlbumsState.Loading)
        private set

    init {
        AlbumsAction.FetchAlbums.let(::handleAction)
    }

    fun handleAction(action: AlbumsAction) {
        when (action) {
            is AlbumsAction.OnAlbumClick -> {}
            is AlbumsAction.OnErrorActionClick -> fetchArtistsList()
            is AlbumsAction.FetchAlbums -> fetchArtistsList()
        }
    }

    private fun fetchArtistsList() {
        launch(
            body = {
                state = AlbumsState.Loading
                state = repository.getAllAlbums()
                    .map {
                        VerticalCardVs(
                            id = it.id,
                            title = it.title,
                            imageUrl = it.cover,
                            description = it.year,
                        )
                    }
                    .toImmutableList()
                    .let(AlbumsState::List)
            },
            onFailure = {
                state = AlbumsState.Error(
                    message = it.message.orEmpty(),
                    action = "Retry",
                )
            },
        )
    }
}
