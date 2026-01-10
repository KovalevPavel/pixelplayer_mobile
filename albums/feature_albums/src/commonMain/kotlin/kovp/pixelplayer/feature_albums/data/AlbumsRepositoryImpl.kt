package kovp.pixelplayer.feature_albums.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kovp.pixelplayer.domain_albums.AlbumVo
import kovp.pixelplayer.domain_albums.AlbumsRepository

class AlbumsRepositoryImpl(
    private val client: HttpClient,
) : AlbumsRepository {
    override suspend fun getAllAlbums(): List<AlbumVo> {
        return client.get<List<AlbumDto>>(path = "albums/all")
            .mapNotNull { dto ->
                AlbumVo(
                    id = dto.id ?: return@mapNotNull null,
                    title = dto.title.orEmpty(),
                    cover = dto.cover.orEmpty(),
                    year = dto.year.orEmpty(),
                )
            }
    }

    override suspend fun getAlbum(albumId: String): AlbumVo {
        val dto = client.get<AlbumDto>(
            path = "albums/get",
            params = mapOf("album_id" to albumId),
        )

        return AlbumVo(
            id = dto.id.orEmpty(),
            title = dto.title.orEmpty(),
            cover = dto.cover.orEmpty(),
            year = dto.year.orEmpty(),
            tracks = dto.tracks?.mapNotNull {
                AlbumVo.TrackVo(
                    id = it.id ?: return@mapNotNull null,
                    title = it.title.orEmpty(),
                    position = it.position ?: 0,
                )
            }
                .orEmpty(),
        )
    }
}

@Serializable
private class AlbumDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("cover")
    val cover: String? = null,
    @SerialName("year")
    val year: String? = null,
    @SerialName("tracks")
    val tracks: List<TrackDto>? = null,
)

@Serializable
private class TrackDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("position")
    val position: Int? = null,
)
