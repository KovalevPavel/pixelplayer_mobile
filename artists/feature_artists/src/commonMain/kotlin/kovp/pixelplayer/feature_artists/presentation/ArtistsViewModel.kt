package kovp.pixelplayer.feature_artists.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_artists.ArtistsRepository

internal class ArtistsViewModel(
    private val repository: ArtistsRepository,
) : ViewModel() {
    var state: ArtistsState by mutableStateOf(ArtistsState.Loading)
        private set

    init {
        ArtistsAction.FetchArtists.let(::handleAction)
    }

    fun handleAction(action: ArtistsAction) {
        when (action) {
            is ArtistsAction.OnArtistClick -> {}
            is ArtistsAction.OnErrorActionClick -> fetchArtistsList()
            is ArtistsAction.FetchArtists -> fetchArtistsList()
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
                            description = "Albums: ${it.albums}",
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
}
