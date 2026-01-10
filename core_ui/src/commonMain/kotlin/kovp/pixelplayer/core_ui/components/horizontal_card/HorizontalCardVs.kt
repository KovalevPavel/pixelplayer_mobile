package kovp.pixelplayer.core_ui.components.horizontal_card

data class HorizontalCardVs(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val payload: Any? = null,
)
