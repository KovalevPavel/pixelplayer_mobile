package kovp.pixelplayer.api_settings.di

import kovp.pixelplayer.feature_settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

object SettingsScope

val settingsScopeModule = module {
    scope<SettingsScope> {
        scopedOf(::SettingsViewModel)
    }
}
