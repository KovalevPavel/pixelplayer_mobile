package kovp.pixelplayer.feature_tracks.presentation

import kovp.pixelplayer.core_ui.components.player.PlayerVs

internal sealed interface TracksAction {
    data object OnErrorActionClick : TracksAction
    data object FetchTracks : TracksAction
    data class OnTrackClick(
        val trackId: String,
        val metadata: PlayerVs.TrackMetaData?,
    ) : TracksAction
}
