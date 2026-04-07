package kovp.pixelplayer.core.language

interface AppLanguageManager {
    val supportsOverride: Boolean

    fun applySelection(selection: LanguageSelection)

    fun resolveDeviceLanguage(): AppLanguage
}
