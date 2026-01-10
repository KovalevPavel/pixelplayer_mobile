package kovp.pixelplayer.core_player

import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kovp.pixelplayer.core.context.AndroidAppContext
import kovp.pixelplayer.core_player.data.TrackMetaData

internal class AndroidAudioController(
    private val context: AndroidAppContext,
    sessionToken: SessionToken,
    private val baseUrl: String,
) {
    var isInitialized: Boolean = false
        private set
    val currentPosition: Long get() = controller.currentPosition
    val duration: Long get() = controller.duration
    val currentId: String? get() = controller.currentMediaItem?.mediaId
    val currentTrack: String? get() = controller.currentMediaItem?.mediaMetadata?.title?.toString()
    val currentAlbum: String? get() = controller.currentMediaItem?.mediaMetadata?.albumTitle?.toString()
    val isPlaying: Boolean get() = controller.isPlaying

    private val listeners = mutableListOf<Player.Listener>()

    private val controllerFuture = MediaController.Builder(context.context, sessionToken)
        .setListener(
            object : MediaController.Listener {
                override fun onDisconnected(controller: MediaController) {
                    listeners.forEach(controller::removeListener)
                    super.onDisconnected(controller)
                }
            }
        )
        .buildAsync()
        .also { future ->
            future.addListener(
                { isInitialized = true },
                ContextCompat.getMainExecutor(context.context),
            )
        }

    private val controller: MediaController by lazy {
        controllerFuture.get()
    }

    fun play(
        id: String,
        metadata: TrackMetaData?,
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

    fun pause() {
        controller.pause()
    }

    fun stop() {
        controller.stop()
    }

    fun next() {
        controller.seekToNext()
    }

    fun prev() {
        controller.seekToPrevious()
    }

    fun seekTo(position: Long) {
        controller.seekTo(position)
    }

    private fun mapUrl(uri: String): String {
        return "$baseUrl/api/play/$uri"
    }
}
