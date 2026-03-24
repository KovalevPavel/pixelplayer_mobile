package kovp.pixelplayer.feature_artists.data

import io.ktor.client.HttpClient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.network.get
import kovp.pixelplayer.core.orZero
import kovp.pixelplayer.domain_artists.ArtistVo
import kovp.pixelplayer.domain_artists.ArtistsRepository

class ArtistsRepositoryImpl(
    private val client: HttpClient,
) : ArtistsRepository {
    override suspend fun getAllArtists(): List<ArtistVo> {
        return client.get<List<ArtistDto>>(path = "artists/all")
            .mapNotNull(::mapToDomain)
    }

    override suspend fun getArtistInfo(artistId: String): ArtistVo {
        return client.get<ArtistDto>(
            path = "artists/get",
            params = mapOf(
                "artist_id" to artistId,
            ),
        )
            .let { dto -> mapToDomain(dto, artistId) }
    }

    private fun mapToDomain(dto: ArtistDto): ArtistVo? {
        return mapToDomain(
            dto = dto,
            artistId = dto.id ?: return null,
        )
    }

    private fun mapToDomain(dto: ArtistDto, artistId: String): ArtistVo {
        return ArtistVo(
            id = artistId,
            name = dto.name.orEmpty(),
            avatar = dto.avatar.orEmpty(),
            albums = dto.albums?.mapNotNull {
                ArtistVo.ArtistAlbumVo(
                    id = it.id ?: return@mapNotNull null,
                    title = it.title.orEmpty(),
                    cover = it.cover.orEmpty(),
                    year = it.year.orEmpty(),
                    tracks = it.tracks?.filter { tr -> tr.id != null }
                        ?.size
                        .orZero(),
                )
            }
                .orEmpty(),
        )
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
)
