package kov_p.pixelplayer.feature_albums.di

import kov_p.pixelplayer.feature_albums.list.AlbumsViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

object AlbumsScope

internal val albumsModule = module {
    scope<AlbumsScope> {
        scopedOf(::AlbumsViewModel)
    }
}
