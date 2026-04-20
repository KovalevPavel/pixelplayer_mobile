package kovp.pixelplayer.androidApp.di

import kovp.pixelplayer.core.build_config.BuildConfig
import org.koin.dsl.module

val buildConfigModule = module {
    single<BuildConfig> {
        object : BuildConfig {
            override val isDebug: Boolean = kovp.pixelplayer.BuildConfig.DEBUG
            override val isDemoApp: Boolean = kovp.pixelplayer.BuildConfig.FLAVOR == "demo"
        }
    }
}
