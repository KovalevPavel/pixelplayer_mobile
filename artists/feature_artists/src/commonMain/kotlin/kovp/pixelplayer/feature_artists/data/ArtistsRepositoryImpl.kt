package kovp.pixelplayer.feature_artists.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kovp.pixelplayer.domain_artists.ArtistVo
import kovp.pixelplayer.domain_artists.ArtistsRepository

class ArtistsRepositoryImpl(
    private val client: HttpClient,
) : ArtistsRepository {
    override suspend fun getAllArtists(): List<ArtistVo> {
        return client.get<List<ArtistDto>>(path = "artists/all")
            .mapNotNull { dto ->
                ArtistVo(
                    id = dto.id ?: return@mapNotNull null,
                    name = dto.name.orEmpty(),
                    avatar = dto.avatar.orEmpty(),
                    albums = dto.albums?.mapNotNull { it.id ?: return@mapNotNull null }?.size
                        ?: 0,
                )
            }
    }
}

@Serializable
private class ArtistDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("avatar_url")
    val avatar: String? = null,
    @SerialName("albums")
    val albums: List<AlbumDto>? = null,
)

@Serializable
private class AlbumDto(
    @SerialName("id")
    val id: String? = null,
)
