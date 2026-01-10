package kovp.pixelplayer.core_player

import kotlinx.coroutines.flow.StateFlow
import kovp.pixelplayer.core_ui.components.player.PlayerVs

interface Player {
    val playerVs: StateFlow<PlayerVs>

    fun play(
        id: String,
        metadata: PlayerVs.TrackMetaData?,
    )

    fun resume()
    fun pause()
    fun next()
    fun previous()
    fun seekTo(fraction: Float)
}
