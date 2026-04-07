package kovp.pixelplayer.core.language

import kovp.pixelplayer.api_storage.Preferences

class AppLanguageRepository(
    private val preferences: Preferences,
) {
    suspend fun getSelection(): LanguageSelection {
        return AppLanguage.fromCode(preferences.getString(LANGUAGE_KEY))
            ?.let(LanguageSelection::Explicit)
            ?: LanguageSelection.System
    }

    suspend fun setSelection(selection: LanguageSelection) {
        when (selection) {
            LanguageSelection.System -> preferences.updateValue(LANGUAGE_KEY, null)
            is LanguageSelection.Explicit -> preferences.updateValue(
                LANGUAGE_KEY,
                selection.language.code,
            )
        }
    }

    companion object {
        private const val LANGUAGE_KEY: String = "app_language"
    }
}
