package kovp.pixelplayer.core_player

import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kovp.pixelplayer.core.context.AndroidAppContext
import kotlin.math.roundToLong

private typealias PlayerImpl = androidx.media3.common.Player

internal class AndroidPlayer(
    private val context: AndroidAppContext,
    private val baseUrl: String,
    sessionToken: SessionToken,
) : Player {
    override val playerVs: StateFlow<PlayerVs> by lazy { _playerState }
    private val _playerState = MutableStateFlow<PlayerVs>(PlayerVs.Empty)

    private val listener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            updatePlayerState()
        }
    }

    private val controllerFuture = MediaController.Builder(context.context, sessionToken)
        .setListener(
            object : MediaController.Listener {
                override fun onDisconnected(controller: MediaController) {
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
                    updatePlayerState()
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
                controller.clearMediaItems()
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
                            .build()
                    )
                    .setUri(url)
                    .build()
            }
                .let(controller::setMediaItems)
            controller.prepare()
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
                                .build()
                        )
                        .setUri(mappedUri)
                        .build()
                }
                .let(controller::setMediaItem)

            controller.prepare()
            controller.play()
        }
    }

    override fun play(index: Int) {
        doIfAvailable(
            PlayerImpl.COMMAND_PLAY_PAUSE,
            PlayerImpl.COMMAND_SEEK_TO_MEDIA_ITEM,
        ) {
            controller.seekTo(index, 0)
            controller.play()
        }
    }

    override fun resume() {
        doIfAvailable(PlayerImpl.COMMAND_PLAY_PAUSE) {
            controller.play()
        }
    }

    override fun pause() {
        doIfAvailable(PlayerImpl.COMMAND_PLAY_PAUSE) {
            controller.pause()
        }
    }

    override fun next() {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_TO_NEXT) {
            controller.seekToNext()
        }
    }

    override fun previous() {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_TO_PREVIOUS) {
            seekToPrevious()
        }
    }

    override fun seekTo(fraction: Float) {
        doIfAvailable(PlayerImpl.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) {
            val newPosition = (controller.duration * fraction).roundToLong()
            seekTo(newPosition)
        }
    }

    private fun updatePlayerState() {
        _playerState.value = run {
            val id = controller.currentMediaItem?.mediaId ?: return@run PlayerVs.Empty
            PlayerVs.Data(
                trackId = id,
                metaData = TrackIn.TrackMetaData(
                    trackTitle = controller.currentMediaItem?.mediaMetadata?.title?.toString(),
                    album = controller.currentMediaItem?.mediaMetadata?.albumTitle?.toString(),
                    artist = controller.currentMediaItem?.mediaMetadata?.artist?.toString(),
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
