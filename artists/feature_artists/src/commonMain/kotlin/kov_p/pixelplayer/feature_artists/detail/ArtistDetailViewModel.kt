package kov_p.pixelplayer.feature_artists.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kov_p.pixelplayer.core_ui.launch
import kov_p.pixelplayer.domain_artists.ArtistsRepository

class ArtistDetailViewModel(
    private val artistId: String,
    private val artistsRepository: ArtistsRepository,
) : ViewModel() {
    val stateFlow: StateFlow<ArtistDetailState> by lazy { _stateFlow }
    val eventsFlow: Flow<ArtistDetailEvent> by lazy { _eventsFlow }

    private val _stateFlow = MutableStateFlow<ArtistDetailState>(ArtistDetailState.Loading)
    private val _eventsFlow = MutableSharedFlow<ArtistDetailEvent>()

    init {
        ArtistDetailAction.FetchData.let(::handleAction)
    }

    fun handleAction(action: ArtistDetailAction) {
        when (action) {
            ArtistDetailAction.FetchData -> {
                fetchData()
            }

            is ArtistDetailAction.OnAlbumClick -> {
                ArtistDetailEvent.NavigateToAlbum(albumId = action.albumId)
                    .let(::emitNewEvent)
            }
        }
    }

    private fun fetchData() {
        launch(
            body = {
                _stateFlow.update { ArtistDetailState.Loading }
                val artist = artistsRepository.getArtistInfo(artistId = artistId)
                _stateFlow.update {
                    ArtistDetailState.Data(
                        artistName = artist.name,
                        avatar = artist.avatar,
                        albums = artist.albums.map { vo ->
                            VerticalCardVs(
                                id = vo.id,
                                imageUrl = vo.cover,
                                title = UiText.Dynamic(vo.title),
                                description = UiText.Dynamic(vo.tracks.toString()),
                                tagline = UiText.Dynamic(vo.year),
                            )
                        }
                            .toImmutableList()
                    )
                }
            },
            onFailure = {
                val message = it.message.orEmpty()
                _stateFlow.update { ArtistDetailState.Error(message = message) }
            },
        )
    }

    private fun emitNewEvent(newEvent: ArtistDetailEvent) {
        viewModelScope.launch { _eventsFlow.emit(newEvent) }
    }
}
