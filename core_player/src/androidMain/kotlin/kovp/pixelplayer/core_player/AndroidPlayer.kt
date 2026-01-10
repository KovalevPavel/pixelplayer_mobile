package kovp.pixelplayer.core_player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kovp.pixelplayer.core_ui.components.player.PlayerVs

internal class AndroidPlayer(
    private val controller: AndroidAudioController,
) : Player {

    private val flowScope = CoroutineScope(Dispatchers.Main)

    override val playerVs = flow {
        while (true) {
            delay(500)

            if (!controller.isInitialized) {
                continue
            }

            val playerState = controller.currentId?.let { track ->
                PlayerVs.Data(
                    trackId = track,
                    metaData = PlayerVs.TrackMetaData(
                        trackTitle = controller.currentTrack,
                        album = controller.currentAlbum,
                        artist = controller.currentArtist,
                    ),
                    isPlaying = controller.isPlaying,
                    timeLine = PlayerVs.AudioTimeline(
                        currentPositionMs = controller.currentPosition,
                        durationMs = controller.duration,
                    ),
                    hasNext = controller.hasNext,
                )
            }
                ?: PlayerVs.Empty

            emit(playerState)
        }
    }
        .stateIn(
            scope = flowScope,
            started = SharingStarted.Eagerly,
            initialValue = PlayerVs.Empty,
        )

    override fun play(
        id: String,
        metadata: PlayerVs.TrackMetaData?,
    ) {
        controller.play(
            id = id,
            metadata = metadata,
        )
    }

    override fun resume() {
        controller.resume()
    }

    override fun pause() {
        controller.pause()
    }

    override fun next() {
        controller.next()
    }

    override fun previous() {
        controller.prev()
    }

    override fun seekTo(fraction: Float) {
        controller.seekTo(fraction = fraction)
    }
}
