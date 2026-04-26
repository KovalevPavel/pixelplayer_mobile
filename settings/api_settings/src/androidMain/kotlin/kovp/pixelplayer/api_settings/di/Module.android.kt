package kovp.pixelplayer.api_settings.di

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import kovp.pixelplayer.core.language.AppLanguage
import kovp.pixelplayer.core.language.AppLanguageManager
import kovp.pixelplayer.core.language.LanguageSelection
import org.koin.core.module.Module
import org.koin.dsl.module

private class AndroidAppLanguageManager(
    private val context: Context,
) : AppLanguageManager {
    override val supportsOverride: Boolean = true

    override fun applySelection(selection: LanguageSelection) {
        if (isSelectionApplied(selection)) return

        when (selection) {
            LanguageSelection.System -> {
                LocaleListCompat.getEmptyLocaleList()
            }

            is LanguageSelection.Explicit -> {
                LocaleListCompat.forLanguageTags(selection.language.code)
            }
        }
            .let(AppCompatDelegate::setApplicationLocales)
    }

    override fun isSelectionApplied(selection: LanguageSelection): Boolean {
        val appliedTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()

        val currentSelection = if (appliedTags.isBlank()) {
            LanguageSelection.System
        } else {
            appliedTags.substringBefore(',')
                .substringBefore('-')
                .let(AppLanguage::fromCode)
                ?.let(LanguageSelection::Explicit)
                ?: LanguageSelection.System
        }

        return currentSelection == selection
    }

    override fun resolveDeviceLanguage(): AppLanguage {
        return LocaleManagerCompat.getSystemLocales(context)
            .getFirstMatch(AppLanguage.entries.map { it.code }.toTypedArray())
            ?.language
            .let(AppLanguage::fromCode)
            ?: AppLanguage.English
    }
}

actual val appLanguagePlatformModule: Module = module {
    single<AppLanguageManager> { AndroidAppLanguageManager(context = get()) }
}
