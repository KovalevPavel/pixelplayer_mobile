package kovp.pixelplayer.core_player

import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kovp.pixelplayer.core.context.AndroidAppContext
import kotlin.math.roundToLong

private typealias PlayerImpl = androidx.media3.common.Player
private const val PROGRESS_UPDATE_INTERVAL_MS = 250L

internal class AndroidPlayer(
    private val context: AndroidAppContext,
    private val baseUrl: String,
    sessionToken: SessionToken,
) : Player {
    override val playerVs: StateFlow<PlayerVs> by lazy { _playerState }
    private val _playerState = MutableStateFlow<PlayerVs>(PlayerVs.Empty)
    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressUpdateJob: Job? = null

    private val listener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            syncPlayerStateAndProgressUpdates()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            syncPlayerStateAndProgressUpdates()
        }

        override fun onPositionDiscontinuity(
            oldPosition: androidx.media3.common.Player.PositionInfo,
            newPosition: androidx.media3.common.Player.PositionInfo,
            reason: Int,
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            syncPlayerStateAndProgressUpdates()
        }

        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            syncPlayerStateAndProgressUpdates()
        }
    }

    private val controllerFuture = MediaController.Builder(context.context, sessionToken)
        .setListener(
            object : MediaController.Listener {
                override fun onDisconnected(controller: MediaController) {
                    stopProgressUpdates()
                    playerScope.cancel()
                    controller.removeListener(listener)
                    super.onDisconnected(controller)
                }
            }
        )
        .buildAsync()
        .also { future ->
            future.addListener(
                {
                    controller.addListener(listener)
                    syncPlayerStateAndProgressUpdates()
                },
                ContextCompat.getMainExecutor(context.context),
            )
        }

    private val controller: MediaController by lazy {
        controllerFuture.get()
    }

    override fun loadTracks(tracks: List<TrackIn>, clear: Boolean) {
        doIfAvailable(
            PlayerImpl.COMMAND_SET_MEDIA_ITEM,
            PlayerImpl.COMMAND_PREPARE,
            PlayerImpl.COMMAND_CHANGE_MEDIA_ITEMS,
        ) {
            if (clear) {
                clearMediaItems()
            }

            tracks.map { t ->
                val url = mapUrl(t.trackId)

                MediaItem.Builder()
                    .setMediaId(t.trackId)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(t.metadata?.trackTitle)
                            .setAlbumTitle(t.metadata?.album)
                            .setArtist(t.metadata?.artist)
                            .setDiscNumber(t.metadata?.disk)
                            .setTrackNumber(t.metadata?.position)
                            .build()
                    )
                    .setUri(url)
                    .build()
            }
                .let(::setMediaItems)
            prepare()
        }
    }

    override fun play(
        id: String,
        metadata: TrackIn.TrackMetaData?,
    ) {
        doIfAvailable(
            PlayerImpl.COMMAND_SET_MEDIA_ITEM,
            PlayerImpl.COMMAND_PREPARE,
            PlayerImpl.COMMAND_PLAY_PAUSE,
        ) {
            id.let(::mapUrl)
                .let { mappedUri ->
                    MediaItem.Builder()
                        .setMediaId(id)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(metadata?.trackTitle)
                                .setAlbumTitle(metadata?.album)
                                .setArtist(metadata?.artist)
                                .setDiscNumber(metadata?.disk)
                                .setTrackNumber(metadata?.position)
                                .build()
                        )
                        .setUri(mappedUri)
                        .build()
                }
                .let(::setMediaItem)

            prepare()
            play()
        }
    }

    override fun play(index: Int) {
        doIfAvailable(
            PlayerImpl.COMMAND_PLAY_PAUSE,
            PlayerImpl.COMMAND_SEEK_TO_MEDIA_ITEM,
        ) {
            seekTo(index, 0)
            play()
        }
    }

    override fun resume() {
        doIfAvailable(PlayerImpl.COMMAND_PLAY_PAUSE) {
            play()
        }
    }

    override fun pause() {
        doIfAvailable(PlayerImpl.COMMAND_PLAY_PAUSE) {
            pause()
            syncPlayerStateAndProgressUpdates()
        }
    }

    override fun next() {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_TO_NEXT) {
            seekToNext()
        }
    }

    override fun previous() {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_TO_PREVIOUS) {
            seekToPrevious()
        }
    }

    override fun seekTo(fraction: Float) {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) {
            val newPosition = (duration * fraction).roundToLong()
            seekTo(newPosition)
        }
    }

    override fun clearPlayer() {
        doIfAvailable(PlayerImpl.COMMAND_STOP) {
            stopProgressUpdates()
            stop()
            clearMediaItems()
            updatePlayerState()
        }
    }

    private fun syncPlayerStateAndProgressUpdates() {
        updatePlayerState()

        if (controller.isPlaying && controller.currentMediaItem != null) {
            startProgressUpdates()
        } else {
            stopProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = playerScope.launch {
            while (isActive && controller.isPlaying && controller.currentMediaItem != null) {
                updatePlayerState()
                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun updatePlayerState() {
        _playerState.update {
            val id = controller.currentMediaItem?.mediaId ?: return@update PlayerVs.Empty
            PlayerVs.Data(
                trackId = id,
                metaData = TrackIn.TrackMetaData(
                    trackTitle = controller.currentMediaItem?.mediaMetadata?.title?.toString(),
                    album = controller.currentMediaItem?.mediaMetadata?.albumTitle?.toString(),
                    artist = controller.currentMediaItem?.mediaMetadata?.artist?.toString(),
                    disk = controller.currentMediaItem?.mediaMetadata?.discNumber,
                    position = controller.currentMediaItem?.mediaMetadata?.trackNumber,
                ),
                timeLine = PlayerVs.AudioTimeline(
                    isPlaying = controller.isPlaying,
                    currentPositionMs = controller.currentPosition,
                    durationMs = controller.duration,
                ),
                hasNext = controller.hasNextMediaItem(),
            )
        }
    }

    private fun doIfAvailable(vararg command: Int, action: MediaController.() -> Unit) {
        if (command.all(controller::isCommandAvailable)) {
            controller.action()
        }
    }

    private fun mapUrl(uri: String): String {
        return "$baseUrl/api/play/$uri"
    }
}
