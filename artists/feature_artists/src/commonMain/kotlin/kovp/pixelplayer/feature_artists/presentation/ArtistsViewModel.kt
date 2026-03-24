package kovp.pixelplayer.feature_artists.presentation

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
import kovp.pixelplayer.domain_artists.ArtistsRepository

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
                            title = it.name,
                            imageUrl = it.avatar,
                            description = "Albums: ${it.albums.size}",
                        )
                    }
                    .toImmutableList()
                    .let(ArtistsState::List)
            },
            onFailure = {
                state = ArtistsState.Error(
                    message = it.message.orEmpty(),
                    action = "Retry",
                )
            },
        )
    }

    private fun emitNewEvent(newEvent: ArtistsEvent) {
        viewModelScope.launch { _artistsEvents.emit(newEvent) }
    }
}
