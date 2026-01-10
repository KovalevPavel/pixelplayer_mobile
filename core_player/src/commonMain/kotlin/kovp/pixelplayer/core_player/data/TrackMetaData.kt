package kovp.pixelplayer.core_player.data

import kotlinx.serialization.Serializable

@Serializable
data class TrackMetaData(
    val trackTitle: String? = null,
    val album: String? = null,
    val artist: String? = null,
)
