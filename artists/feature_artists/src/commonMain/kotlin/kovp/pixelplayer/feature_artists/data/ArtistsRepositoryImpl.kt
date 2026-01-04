package kovp.pixelplayer.feature_artists.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kovp.pixelplayer.domain_artists.ArtistVo
import kovp.pixelplayer.domain_artists.ArtistsRepository

class ArtistsRepositoryImpl(
    private val client: HttpClient,
): ArtistsRepository {
    override suspend fun getAllArtists(): List<ArtistVo> {
        return client.get<List<ArtistDto>>(path = "artists/all")
            .mapNotNull { dto ->
                ArtistVo(
                    id = dto.id ?: return@mapNotNull null,
                    name = dto.name.orEmpty(),
                    avatar = dto.avatar.orEmpty(),
                )
            }
    }
}

@Serializable
private class ArtistDto(
    @SerialName("id")
    val id: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("avatar_url")
    val avatar: String?,
)
