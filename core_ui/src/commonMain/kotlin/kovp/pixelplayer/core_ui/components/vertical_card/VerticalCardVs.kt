package kovp.pixelplayer.core_ui.components.vertical_card

import kovp.pixelplayer.core_ui.UiText

data class VerticalCardVs(
    val id: String,
    val imageUrl: String,
    val title: UiText,
    val description: UiText,
    val tagline: UiText? = null,
)
