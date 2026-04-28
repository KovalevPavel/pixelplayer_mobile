package kov_p.pixelplayer.api_settings.di

import kov_p.pixelplayer.core.language.AppLanguage
import kov_p.pixelplayer.core.language.AppLanguageManager
import kov_p.pixelplayer.core.language.LanguageSelection
import org.koin.core.module.Module
import org.koin.dsl.module

private class IosAppLanguageManager : AppLanguageManager {
    override val supportsOverride: Boolean = false

    override fun applySelection(selection: LanguageSelection) = Unit
    override fun isSelectionApplied(selection: LanguageSelection): Boolean = true

    override fun resolveDeviceLanguage(): AppLanguage = AppLanguage.English
}

actual val appLanguagePlatformModule: Module = module {
    single<AppLanguageManager> { IosAppLanguageManager() }
}
