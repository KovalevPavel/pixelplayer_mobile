package kovp.pixelplayer.core_player

import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kovp.pixelplayer.core.context.AndroidAppContext

internal class AndroidAudioController(
    private val context: AndroidAppContext,
    sessionToken: SessionToken,
    private val baseUrl: String,
) {
    val isInitialized: StateFlow<Boolean> by lazy { _isInitialized }
    val currentPosition: Long get() = controller.currentPosition
    val duration: Long get() = controller.duration

    private val _isInitialized = MutableStateFlow(false)
    private val listeners = mutableListOf<Player.Listener>()

    private val controllerFuture =
        MediaController.Builder(context.context, sessionToken)
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
                    { _isInitialized.value = true },
                    ContextCompat.getMainExecutor(context.context),
                )
            }

    private val controller: MediaController by lazy {
        controllerFuture.get()
    }

    fun addListener(listener: Player.Listener) {
        controller.addListener(listener)
        listeners.add(listener)
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
