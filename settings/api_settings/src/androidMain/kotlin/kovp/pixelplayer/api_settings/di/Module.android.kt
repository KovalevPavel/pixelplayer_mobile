package kovp.pixelplayer.api_settings.di

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kovp.pixelplayer.core.language.AppLanguage
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.LanguageSelection
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.Locale

private class AndroidAppLanguageManager : AppLanguageManager {
    override val supportsOverride: Boolean = true

    override fun applySelection(selection: LanguageSelection) {
        when (selection) {
            LanguageSelection.System -> {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }

            is LanguageSelection.Explicit -> {
                LocaleListCompat.forLanguageTags(selection.language.code)
                    .let(AppCompatDelegate::setApplicationLocales)
            }
        }
    }

    override fun resolveDeviceLanguage(): AppLanguage {
        return AppLanguage.fromCode(Locale.getDefault().language)
            ?: AppLanguage.English
    }
}

actual val appLanguagePlatformModule: Module = module {
    single<AppLanguageManager> { AndroidAppLanguageManager() }
}
