package kov_p.pixelplayer.feature_artists.di

import kov_p.pixelplayer.feature_artists.presentation.ArtistsViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

object ArtistsScope

internal val artistsModule = module {
    scope<ArtistsScope> {
        scopedOf(::ArtistsViewModel)
    }
}
