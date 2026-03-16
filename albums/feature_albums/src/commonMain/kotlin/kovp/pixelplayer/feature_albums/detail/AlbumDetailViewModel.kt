package kovp.pixelplayer.feature_albums.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_player.Player
import kovp.pixelplayer.core_player.TrackIn
import kovp.pixelplayer.core_ui.launch
import kovp.pixelplayer.domain_albums.AlbumVo
import kovp.pixelplayer.domain_albums.AlbumsRepository
import kotlin.time.Duration

class AlbumDetailViewModel(
    private val albumId: String,
    private val repository: AlbumsRepository,
    private val player: Player,
) : ViewModel() {
    var viewState: AlbumDetailState by mutableStateOf(AlbumDetailState.Loading)
        private set

    val playerVs = player.playerVs

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
                var currentGlobalIndex: Int = -1

                viewState = AlbumDetailState.Data(
                    title = album.title,
                    artist = album.artist,
                    year = album.year,
                    cover = album.cover,
                    disks = album.tracks
                        .groupBy { it.disk }
                        .map { (disk, tracks) ->
                            AlbumDetailState.Disk(
                                diskNumber = disk,
                                tracks = tracks
                                    .sortedBy(AlbumVo.TrackVo::position)
                                    .map { tr ->
                                        currentGlobalIndex += 1
                                        AlbumDetailState.TrackVs(
                                            id = tr.id,
                                            title = tr.title,
                                            position = tr.position + 1,
                                            globalPosition = currentGlobalIndex,
                                            artist = album.artist,
                                            duration = mapDuration(tr.duration),
                                            quality = tr.quality,
                                        )
                                    }
                                    .toImmutableList(),
                            )
                        }
                        .sortedBy(AlbumDetailState.Disk::diskNumber)
                        .toImmutableList(),
                )
            },
        )
    }

    private fun mapDuration(duration: Duration): String {
        val minutes = duration.inWholeMinutes
        val seconds = duration.inWholeSeconds - minutes * 60
        val secondsString = seconds.toString().padStart(2, '0')

        return "$minutes:$secondsString"
    }

    private fun playTrack(trackIndex: Int) {
        (viewState as? AlbumDetailState.Data)?.let { st ->
            if (!tracksAreLoaded) {
                st.disks
                    .map { disk ->
                        disk.tracks.map { track ->
                            TrackIn(
                                trackId = track.id,
                                metadata = TrackIn.TrackMetaData(
                                    trackTitle = track.title,
                                    album = st.title,
                                    artist = st.artist,
                                    disk = disk.diskNumber.takeIf { st.disks.size > 1 },
                                    position = track.position,
                                ),
                            )
                        }
                    }
                    .flatten()
                    .let(player::loadTracks)
                tracksAreLoaded = true
            }

            player.play(index = trackIndex)
        }
    }
}
