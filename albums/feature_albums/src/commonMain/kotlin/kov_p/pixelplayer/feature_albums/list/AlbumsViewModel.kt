package kov_p.pixelplayer.feature_albums.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kov_p.pixelplayer.core_ui.launch
import kov_p.pixelplayer.domain_albums.AlbumsRepository
import pixelplayer.core_ui.generated.resources.Res as coreRes
import pixelplayer.core_ui.generated.resources.retry

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
                            title = UiText.Dynamic(it.title),
                            imageUrl = it.cover,
                            description = UiText.Dynamic(it.artist),
                            tagline = UiText.Dynamic(it.year),
                        )
                    }
                    .toImmutableList()
                    .let(AlbumsState::List)
            },
            onFailure = {
                state = AlbumsState.Error(
                    message = UiText.Dynamic(it.message.orEmpty()),
                    action = UiText.Resource(coreRes.string.retry),
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
