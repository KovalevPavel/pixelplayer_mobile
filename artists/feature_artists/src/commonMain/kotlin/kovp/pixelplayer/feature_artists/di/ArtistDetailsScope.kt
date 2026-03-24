package kovp.pixelplayer.feature_artists.di

import kovp.pixelplayer.feature_artists.detail.ArtistDetailViewModel
import org.koin.dsl.module

internal object ArtistDetailsScope

internal val detailsModule = module {
    scope<ArtistDetailsScope> {
        scoped {
            ArtistDetailViewModel(
                artistId = it.get(),
                artistsRepository = get(),
            )
        }
    }
}
