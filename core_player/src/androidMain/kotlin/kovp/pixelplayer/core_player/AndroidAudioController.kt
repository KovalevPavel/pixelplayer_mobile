package kovp.pixelplayer.core_player

import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kovp.pixelplayer.core.context.AndroidAppContext
import kotlin.math.roundToLong

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
    val currentArtist: String? get() = controller.currentMediaItem?.mediaMetadata?.artist?.toString()
    val isPlaying: Boolean get() = controller.isPlaying
    val hasNext: Boolean get() = controller.hasNextMediaItem()

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

    fun loadTracks(tracks: List<TrackIn>, clear: Boolean = true) {
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

    fun play(index: Int) {
        controller.seekTo(index, 0)
        controller.play()
    }

    fun play(
        id: String,
        metadata: TrackIn.TrackMetaData?,
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

    fun resume() {
        controller.play()
    }

    fun pause() {
        controller.pause()
    }

    fun next() {
        controller.seekToNext()
    }

    fun prev() {
        controller.seekToPrevious()
    }

    fun seekTo(fraction: Float) {
        val newPosition = (controller.duration * fraction).roundToLong()
        controller.seekTo(newPosition)
    }

    private fun mapUrl(uri: String): String {
        return "$baseUrl/api/play/$uri"
    }
}
