package kov_p.pixelplayer.core.language

enum class AppLanguage(
    val code: String,
) {
    English(code = "en"),
    Russian(code = "ru"),
    German(code = "de");

    companion object {
        fun fromCode(code: String?): AppLanguage? = entries.firstOrNull { it.code == code }
    }
}
