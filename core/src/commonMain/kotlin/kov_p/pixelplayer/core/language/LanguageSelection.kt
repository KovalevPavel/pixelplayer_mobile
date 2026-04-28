package kov_p.pixelplayer.core.language

sealed interface LanguageSelection {
    data object System : LanguageSelection

    data class Explicit(
        val language: AppLanguage,
    ) : LanguageSelection
}
