package kovp.pixelplayer.domain_tracks

interface TracksRepository {
    suspend fun getAllTracks(): List<TrackVo>
}
