package kovp.pixelplayer.feature_tracks.di

import kov_p.pixelplayer.network.di.authorizedClient
import kovp.pixelplayer.domain_tracks.TracksRepository
import kovp.pixelplayer.feature_tracks.data.TracksRepositoryImpl
import kovp.pixelplayer.feature_tracks.presentation.TracksViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.bind
import org.koin.dsl.module

object TracksScope

internal val tracksModule = module {
    scope<TracksScope> {
        scoped {
            TracksRepositoryImpl(client = get(qualifier = authorizedClient))
        }
            .bind<TracksRepository>()

        scopedOf(::TracksViewModel)
    }
}
