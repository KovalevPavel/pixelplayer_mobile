package kovp.pixelplayer.feature_albums.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_player.Player
import kovp.pixelplayer.core_player.TrackIn
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCardVs
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_albums.AlbumsRepository

class AlbumDetailViewModel(
    private val albumId: String,
    private val repository: AlbumsRepository,
    private val player: Player,
) : ViewModel() {
    var viewState: AlbumDetailState by mutableStateOf(AlbumDetailState.Loading)
        private set

    private var tracksAreLoaded = false

    init {
        AlbumDetailAction.FetchTracks.let(::handleAction)
    }

    fun handleAction(action: AlbumDetailAction) {
        when (action) {
            AlbumDetailAction.FetchTracks -> {
                fetchTracks()
            }

            is AlbumDetailAction.OnTrackClick -> {
                playTrack(trackIndex = action.index)
            }
        }
    }

    private fun fetchTracks() {
        launch(
            body = {
                val album = repository.getAlbum(albumId = albumId)
                viewState = AlbumDetailState.Data(
                    title = album.title,
                    artist = "artist",
                    year = album.year,
                    tracks = album.tracks.map {
                        HorizontalCardVs(
                            id = it.id,
                            title = it.title,
                            description = it.position.toString(),
                            imageUrl = "",
                            payload = TrackIn.TrackMetaData(
                                trackTitle = it.title,
                                album = album.title,
                            )
                        )
                    }
                        .toImmutableList(),
                )
            },
        )
    }

    private fun playTrack(trackIndex: Int) {
        (viewState as? AlbumDetailState.Data)?.let { st ->
            if (!tracksAreLoaded) {
                st.tracks.map {
                    TrackIn(
                        trackId = it.id,
                        metadata = it.payload as? TrackIn.TrackMetaData,
                    )
                }
                    .let(player::loadTracks)
                tracksAreLoaded = true
            }

            player.play(index = trackIndex)
        }
    }
}
