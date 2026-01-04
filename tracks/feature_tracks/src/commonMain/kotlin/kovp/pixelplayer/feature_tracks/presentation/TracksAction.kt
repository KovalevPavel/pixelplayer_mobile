package kovp.pixelplayer.feature_tracks.presentation

internal sealed interface TracksAction {
    data object OnErrorActionClick : TracksAction
    data object FetchTracks : TracksAction
}
