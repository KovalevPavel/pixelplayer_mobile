package kovp.pixelplayer.api_albums

import kov_p.pixelplayer.network.di.authorizedClient
import kovp.pixelplayer.domain_albums.AlbumsRepository
import kovp.pixelplayer.feature_albums.data.AlbumsRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val albumsRepoModule = module {
    single {
        AlbumsRepositoryImpl(client = get(qualifier = authorizedClient))
    }
        .bind<AlbumsRepository>()
}
