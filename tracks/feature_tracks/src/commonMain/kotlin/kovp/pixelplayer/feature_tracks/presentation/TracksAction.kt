package kovp.pixelplayer.feature_tracks.presentation

import kovp.pixelplayer.core_player.TrackIn

internal sealed interface TracksAction {
    data object OnErrorActionClick : TracksAction
    data object FetchTracks : TracksAction
    data class OnTrackClick(
        val trackId: String,
        val metadata: TrackIn.TrackMetaData?,
    ) : TracksAction
}
