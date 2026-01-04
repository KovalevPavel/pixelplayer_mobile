package kovp.pixelplayer.feature_tracks.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kovp.pixelplayer.domain_tracks.TrackVo
import kovp.pixelplayer.domain_tracks.TracksRepository

class TracksRepositoryImpl(
    private val client: HttpClient,
): TracksRepository {
    override suspend fun getAllTracks(): List<TrackVo> {
        return client.get<List<TrackDto>>(path = "tracks/all")
            .mapNotNull { dto ->
                TrackVo(
                    id = dto.id ?: return@mapNotNull null,
                    title = dto.title.orEmpty(),
                    album = dto.album.orEmpty(),
                    artist = dto.artist.orEmpty(),
                    cover = dto.cover.orEmpty(),
                )
            }
    }
}

@Serializable
private class TrackDto(
    @SerialName("id")
    val id: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("album")
    val album: String?,
    @SerialName("artist")
    val artist: String?,
    @SerialName("cover")
    val cover: String?,
)
