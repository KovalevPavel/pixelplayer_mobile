package kovp.pixelplayer.feature_albums.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_albums.AlbumsRepository

internal class AlbumsViewModel(
    private val repository: AlbumsRepository,
) : ViewModel() {
    var state: AlbumsState by mutableStateOf(AlbumsState.Loading)
        private set

    val eventsFlow: Flow<AlbumsEvent> by lazy { _eventsFlow }
    private val _eventsFlow = MutableSharedFlow<AlbumsEvent>()

    init {
        AlbumsAction.FetchAlbums.let(::handleAction)
    }

    fun handleAction(action: AlbumsAction) {
        when (action) {
            is AlbumsAction.OnErrorActionClick,
            is AlbumsAction.FetchAlbums,
            -> {
                fetchAlbumsList()
            }

            is AlbumsAction.OnAlbumClick -> {
                AlbumsEvent.NavigateToAlbum(albumId = action.albumId).let(::emitEvent)
            }
        }
    }

    private fun fetchAlbumsList() {
        launch(
            body = {
                state = AlbumsState.Loading
                state = repository.getAllAlbums()
                    .map {
                        VerticalCardVs(
                            id = it.id,
                            title = it.title,
                            imageUrl = it.cover,
                            description = it.artist,
                            tagline = it.year,
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

    private fun emitEvent(event: AlbumsEvent) {
        viewModelScope.launch {
            _eventsFlow.emit(event)
        }
    }
}
