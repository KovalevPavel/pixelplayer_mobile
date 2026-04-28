package kov_p.pixelplayer.di

import kov_p.pixelplayer.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module(createdAtStart = true) {
    viewModelOf(::MainViewModel)
}
