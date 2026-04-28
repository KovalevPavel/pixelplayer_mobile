package kov_p.pixelplayer.api_settings.di

import kov_p.pixelplayer.core.language.AppLanguageRepository
import kov_p.pixelplayer.feature_settings.presentation.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object SettingsScope

expect val appLanguagePlatformModule: Module

val languageModule = module {
    includes(appLanguagePlatformModule)
    singleOf(::AppLanguageRepository)
}

val settingsScopeModule = module {
    scope<SettingsScope> {
        scopedOf(::SettingsViewModel)
    }
}
