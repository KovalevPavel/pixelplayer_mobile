package kovp.pixelplayer.feature_tracks.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_player.Player
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs
import kovp.pixelplayer.core_player.PlayerVs
import kovp.pixelplayer.core_player.TrackIn
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_tracks.TracksRepository

internal class TracksViewModel(
    private val repository: TracksRepository,
    private val player: Player,
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
            -> {
                fetchTracks()
            }

            is TracksAction.OnTrackClick -> {
                handleOnTrackClick(id = action.trackId, metaData = action.metadata)
            }
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
                            description = "${it.artist} • ${it.album}",
                            payload = TrackIn.TrackMetaData(
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

    private fun handleOnTrackClick(
        id: String,
        metaData: TrackIn.TrackMetaData?,
    ) {
        (player.playerVs.value as? PlayerVs.Data)?.let { st ->
            when (st.trackId) {
                id -> {
                    if (st.isPlaying) {
                        player.pause()
                    } else {
                        player.resume()
                    }
                }

                else -> {
                    player.play(id = id, metadata = metaData)
                }
            }
        }
            ?: run {
                player.play(id = id, metadata = metaData)
            }
    }
}
