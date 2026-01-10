package kovp.pixelplayer.core_player

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kovp.pixelplayer.core_player.data.TrackMetaData
import kovp.pixelplayer.core_ui.components.player.PlayerVs

class PlayerViewModel internal constructor(
    private val player: Player,
) : ViewModel() {
    val playerVs: StateFlow<PlayerVs> = player.playerVs

    private var lastCached: String? = null
    private var cachedPos: Long? = null

    fun play(
        id: String,
        metaData: TrackMetaData?,
    ) {
        if (lastCached == id && cachedPos != null) {
            player.seekTo(cachedPos ?: 0)
        }
        lastCached = null
        cachedPos = null
        player.play(id = id, metadata = metaData)
    }

    fun pause() {
        player.pause()
    }
}
