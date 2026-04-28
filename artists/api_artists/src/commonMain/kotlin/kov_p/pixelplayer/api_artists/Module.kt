package kov_p.pixelplayer.api_artists

import kov_p.pixelplayer.network.di.authorizedClient
import kov_p.pixelplayer.domain_artists.ArtistsRepository
import kov_p.pixelplayer.feature_artists.data.ArtistsRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val artistsRepoModule = module {
    single {
        ArtistsRepositoryImpl(client = get(qualifier = authorizedClient))
    }
        .bind<ArtistsRepository>()
}
