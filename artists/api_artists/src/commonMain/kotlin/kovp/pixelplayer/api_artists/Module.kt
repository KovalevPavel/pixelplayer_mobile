package kovp.pixelplayer.api_artists

import kov_p.pixelplayer.network.di.authorizedClient
import kovp.pixelplayer.domain_artists.ArtistsRepository
import kovp.pixelplayer.feature_artists.data.ArtistsRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val artistsRepoModule = module {
    single {
        ArtistsRepositoryImpl(client = get(qualifier = authorizedClient))
    }
        .bind<ArtistsRepository>()
}
