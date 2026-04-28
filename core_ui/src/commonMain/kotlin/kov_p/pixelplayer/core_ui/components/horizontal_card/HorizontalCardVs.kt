package kov_p.pixelplayer.core_ui.components.horizontal_card

import kov_p.pixelplayer.core_ui.UiText

data class HorizontalCardVs(
    val id: String,
    val imageUrl: String? = null,
    val title: String,
    val description: UiText,
    val payload: Any? = null,
)
