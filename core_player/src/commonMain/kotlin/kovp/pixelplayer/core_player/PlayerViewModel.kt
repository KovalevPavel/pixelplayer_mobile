package kovp.pixelplayer.core_player

import androidx.lifecycle.ViewModel

class PlayerViewModel(
    private val player: Player,
) : ViewModel() {
    fun play(id: String) {
        player.play(id)
    }
}
