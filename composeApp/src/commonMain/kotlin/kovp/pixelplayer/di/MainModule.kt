package kovp.pixelplayer.di

import kovp.pixelplayer.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module(createdAtStart = true) {
    viewModelOf(::MainViewModel)
}
