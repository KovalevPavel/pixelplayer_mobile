package kovp.pixelplayer.core_player

import kotlinx.coroutines.flow.StateFlow
import kovp.pixelplayer.core_player.data.TrackMetaData
import kovp.pixelplayer.core_ui.components.player.PlayerVs

interface Player {
    val playerVs: StateFlow<PlayerVs>

    fun play(
        id: String,
        metadata: TrackMetaData?,
    )

    fun pause()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
}
