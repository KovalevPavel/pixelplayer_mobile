package kovp.pixelplayer.core_ui.components.player

data class PlayerVs(
    val trackTitle: String,
    val album: String,
    val isPlaying: Boolean,
    val totalTime: String,
    val currentTime: String,
    val fraction: Float,
)
