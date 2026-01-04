package kovp.pixelplayer.feature_albums.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kovp.pixelplayer.domain_albums.AlbumVo
import kovp.pixelplayer.domain_albums.AlbumsRepository

class AlbumsRepositoryImpl(
    private val client: HttpClient,
): AlbumsRepository {
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
}

@Serializable
private class AlbumDto(
    @SerialName("id")
    val id: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("cover")
    val cover: String?,
    @SerialName("year")
    val year: String?,
)
