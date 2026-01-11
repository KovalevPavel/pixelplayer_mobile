package kovp.pixelplayer.feature_albums.di

import kov_p.pixelplayer.network.di.authorizedClient
import kovp.pixelplayer.domain_albums.AlbumsRepository
import kovp.pixelplayer.feature_albums.data.AlbumsRepositoryImpl
import kovp.pixelplayer.feature_albums.list.AlbumsViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.bind
import org.koin.dsl.module

object AlbumsScope

internal val albumsModule = module {
    scope<AlbumsScope> {
        scoped {
            AlbumsRepositoryImpl(client = get(qualifier = authorizedClient))
        }
            .bind<AlbumsRepository>()

        scopedOf(::AlbumsViewModel)
    }
}
