package kovp.pixelplayer.feature_tracks.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_player.data.TrackMetaData
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_tracks.TracksRepository

internal class TracksViewModel(
    private val repository: TracksRepository,
) : ViewModel() {
    var state: TracksState by mutableStateOf(TracksState.Loading)
        private set

    init {
        TracksAction.FetchTracks.let(::handleAction)
    }

    fun handleAction(action: TracksAction) {
        when (action) {
            is TracksAction.OnErrorActionClick,
            is TracksAction.FetchTracks,
            -> fetchTracks()
        }
    }

    private fun fetchTracks() {
        launch(
            body = {
                state = TracksState.Loading
                state = repository.getAllTracks()
                    .map {
                        HorizontalCardVs(
                            id = it.id,
                            title = it.title,
                            imageUrl = it.cover,
                            description = "",
                            payload = TrackMetaData(
                                trackTitle = it.title,
                                album = it.album,
                                artist = it.artist,
                            ),
                        )
                    }
                    .toImmutableList()
                    .let(TracksState::List)
            },
            onFailure = {
                state = TracksState.Error(
                    message = it.message.orEmpty(),
                    action = "Retry",
                )
            },
        )
    }
}
