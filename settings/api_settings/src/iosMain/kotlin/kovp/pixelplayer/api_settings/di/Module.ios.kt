package kovp.pixelplayer.api_settings.di

import kovp.pixelplayer.core.language.AppLanguage
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.LanguageSelection
import org.koin.core.module.Module
import org.koin.dsl.module

private class IosAppLanguageManager : AppLanguageManager {
    override val supportsOverride: Boolean = false

    override fun applySelection(selection: LanguageSelection) = Unit

    override fun resolveDeviceLanguage(): AppLanguage = AppLanguage.English
}

actual val appLanguagePlatformModule: Module = module {
    single<AppLanguageManager> { IosAppLanguageManager() }
}
