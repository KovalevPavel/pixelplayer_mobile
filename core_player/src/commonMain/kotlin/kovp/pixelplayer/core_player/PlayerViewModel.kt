package kovp.pixelplayer.core_player

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel internal constructor(
    private val player: Player,
) : ViewModel() {
    val playerVs: StateFlow<PlayerVs> = player.playerVs

    fun handlePlayerAction(action: PlayerAction) {
        when (action) {
            is PlayerAction.Resume -> {
                player.resume()
            }

            is PlayerAction.Pause -> {
                player.pause()
            }

            is PlayerAction.Next -> {
                player.next()
            }


            is PlayerAction.Previous -> {
                player.previous()
            }

            is PlayerAction.Seek -> {
                player.seekTo(action.fraction)
            }
        }
    }
}
