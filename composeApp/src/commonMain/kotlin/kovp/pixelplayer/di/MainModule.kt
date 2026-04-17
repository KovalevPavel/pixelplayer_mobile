package kovp.pixelplayer.di

import kovp.pixelplayer.initializer.InitializerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module(createdAtStart = true) {
    viewModelOf(::InitializerViewModel)
}
