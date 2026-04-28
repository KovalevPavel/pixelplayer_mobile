package kov_p.pixelplayer.feature_artists.presentation

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
import kov_p.pixelplayer.domain_artists.ArtistsRepository
import pixelplayer.core_ui.generated.resources.Res as coreRes
import pixelplayer.core_ui.generated.resources.albums
import pixelplayer.core_ui.generated.resources.retry

internal class ArtistsViewModel(
    private val repository: ArtistsRepository,
) : ViewModel() {
    var state: ArtistsState by mutableStateOf(ArtistsState.Loading)
        private set

    val artistsEvents: Flow<ArtistsEvent> by lazy { _artistsEvents }

    private val _artistsEvents = MutableSharedFlow<ArtistsEvent>()

    init {
        ArtistsAction.FetchArtists.let(::handleAction)
    }

    fun handleAction(action: ArtistsAction) {
        when (action) {
            is ArtistsAction.OnArtistClick -> {
                ArtistsEvent.NavigateToArtist(artistId = action.artistId)
                    .let(::emitNewEvent)
            }

            is ArtistsAction.OnErrorActionClick,
            is ArtistsAction.FetchArtists,
            -> {
                fetchArtistsList()
            }
        }
    }

    private fun fetchArtistsList() {
        launch(
            body = {
                state = ArtistsState.Loading
                state = repository.getAllArtists()
                    .map {
                        VerticalCardVs(
                            id = it.id,
                            title = UiText.Dynamic(it.name),
                            imageUrl = it.avatar,
                            description = UiText.Resource(
                                value = coreRes.string.albums,
                                args = listOf(it.albums.size),
                            ),
                        )
                    }
                    .toImmutableList()
                    .let(ArtistsState::List)
            },
            onFailure = {
                state = ArtistsState.Error(
                    message = UiText.Dynamic(it.message.orEmpty()),
                    action = UiText.Resource(coreRes.string.retry),
                )
            },
        )
    }

    private fun emitNewEvent(newEvent: ArtistsEvent) {
        viewModelScope.launch { _artistsEvents.emit(newEvent) }
    }
}
