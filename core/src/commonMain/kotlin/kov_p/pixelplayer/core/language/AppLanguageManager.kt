package kov_p.pixelplayer.core.language

interface AppLanguageManager {
    val supportsOverride: Boolean

    fun applySelection(selection: LanguageSelection)
    fun isSelectionApplied(selection: LanguageSelection): Boolean

    fun resolveDeviceLanguage(): AppLanguage
}
