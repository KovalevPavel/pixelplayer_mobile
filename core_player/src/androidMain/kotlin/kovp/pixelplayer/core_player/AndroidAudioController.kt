package kovp.pixelplayer.core_player

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kovp.pixelplayer.core_player.context.AndroidAppContext

internal class AndroidAudioController(
    private val context: AndroidAppContext,
    sessionToken: SessionToken,
    private val baseUrl: String,
) {
    private val controllerFuture =
        MediaController.Builder(context.context, sessionToken).buildAsync()

    private val controller: MediaController by lazy {
        controllerFuture.get()
    }

    fun play(uri: String) {
        uri.let(::mapUrl)
            .let(MediaItem::fromUri)
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
