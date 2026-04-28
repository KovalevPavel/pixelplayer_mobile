package kov_p.pixelplayer.androidApp.di

import kov_p.pixelplayer.core.build_config.BuildConfig
import org.koin.dsl.module

val buildConfigModule = module {
    single<BuildConfig> {
        object : BuildConfig {
            override val isDebug: Boolean = kov_p.pixelplayer.BuildConfig.DEBUG
            override val isDemoApp: Boolean = kov_p.pixelplayer.BuildConfig.FLAVOR == "demo"
        }
    }
}
