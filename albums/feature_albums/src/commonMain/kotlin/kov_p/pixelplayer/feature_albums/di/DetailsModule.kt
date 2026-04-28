package kov_p.pixelplayer.feature_albums.di

import kov_p.pixelplayer.feature_albums.detail.AlbumDetailViewModel
import org.koin.dsl.module

internal object AlbumDetailsScope

internal val detailsModule = module {
    scope<AlbumDetailsScope> {
        scoped {
            AlbumDetailViewModel(
                albumId = it.get(),
                repository = get(),
                player = get(),
            )
        }
    }
}
