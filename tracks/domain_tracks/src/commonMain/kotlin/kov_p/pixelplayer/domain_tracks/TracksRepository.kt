package kov_p.pixelplayer.domain_tracks

interface TracksRepository {
    suspend fun getAllTracks(): List<TrackVo>
}
