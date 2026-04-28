package kov_p.pixelplayer.feature_albums.detail

import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kov_p.pixelplayer.core_player.Player
import kov_p.pixelplayer.core_player.PlayerVs
import kov_p.pixelplayer.core_player.TrackIn
import kov_p.pixelplayer.core_ui.launch
import kov_p.pixelplayer.domain_albums.AlbumVo
import kov_p.pixelplayer.domain_albums.AlbumsRepository
import kotlin.time.Duration

class AlbumDetailViewModel(
    private val albumId: String,
    private val repository: AlbumsRepository,
    private val player: Player,
) : ViewModel() {
    val stateFlow: StateFlow<AlbumDetailState> by lazy { _stateFlow }
    val playerVs = player.playerVs

    private val _stateFlow = MutableStateFlow<AlbumDetailState>(AlbumDetailState.Loading)
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

                _stateFlow.update {
                    AlbumDetailState.Data(
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
                }
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
        when (val playerState = player.playerVs.value) {
            is PlayerVs.Data -> {
                handleWhilePlayingTrack(playerState, trackIndex)
            }

            is PlayerVs.Empty -> {
                handleEmptyPlayer(trackIndex)
            }
        }
    }

    private fun handleWhilePlayingTrack(
        playerVs: PlayerVs.Data,
        trackIndex: Int,
    ) {
        when {
            playerVs.metaData.albumId != albumId -> {
                tracksAreLoaded = false
                handleEmptyPlayer(trackIndex)
            }

            else -> {
                handleCurrentAlbum(playerVs = playerVs, index = trackIndex)
            }
        }
    }

    private fun handleEmptyPlayer(
        trackIndex: Int,
    ) {
        (stateFlow.value as? AlbumDetailState.Data)?.let { st ->
            if (!tracksAreLoaded) {
                st.disks.flatMap { disk ->
                    disk.tracks.map { track ->
                        TrackIn(
                            trackId = track.id,
                            metadata = TrackIn.TrackMetaData(
                                trackTitle = track.title,
                                album = st.title,
                                albumId = albumId,
                                artist = st.artist,
                                disk = disk.diskNumber.takeIf { st.disks.size > 1 },
                                position = track.position,
                            ),
                        )
                    }
                }
                    .let(player::loadTracks)
                tracksAreLoaded = true
            }

            player.play(index = trackIndex)
        }
    }

    private fun handleCurrentAlbum(
        playerVs: PlayerVs.Data,
        index: Int,
    ) {
        (stateFlow.value as? AlbumDetailState.Data)?.let { st ->
            val currentLoadedTrack = playerVs.trackId

            val currentLoadedTrackIndex = st.disks.flatMap { it.tracks }
                .firstOrNull { it.id == currentLoadedTrack }
                ?.globalPosition
                ?: return

            when {
                currentLoadedTrackIndex != index -> {
                    player.play(index)
                }

                playerVs.timeLine.isPlaying -> {
                    player.pause()
                }

                else -> {
                    player.resume()
                }
            }
        }
    }
}
