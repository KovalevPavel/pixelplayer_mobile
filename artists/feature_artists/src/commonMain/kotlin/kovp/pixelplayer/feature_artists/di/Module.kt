package kovp.pixelplayer.feature_artists.di

import kov_p.pixelplayer.network.di.authorizedClient
import kovp.pixelplayer.domain_artists.ArtistsRepository
import kovp.pixelplayer.feature_artists.data.ArtistsRepositoryImpl
import kovp.pixelplayer.feature_artists.presentation.ArtistsViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.bind
import org.koin.dsl.module

object ArtistsScope

internal val artistsModule = module {
    scope<ArtistsScope> {
        scoped {
            ArtistsRepositoryImpl(client = get(qualifier = authorizedClient))
        }
            .bind<ArtistsRepository>()

        scopedOf(::ArtistsViewModel)
    }
}
