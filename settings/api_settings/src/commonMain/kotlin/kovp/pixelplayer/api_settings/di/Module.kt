package kovp.pixelplayer.api_settings.di

import kovp.pixelplayer.core.language.AppLanguageRepository
import kovp.pixelplayer.feature_settings.presentation.SettingsViewModel
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
