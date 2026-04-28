package kov_p.pixelplayer.core_ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    val isEmpty: Boolean

    data class Dynamic(val value: String?) : UiText {
        override val isEmpty: Boolean = value.isNullOrEmpty()
    }

    data class Resource(
        val value: StringResource,
        val args: List<Any> = emptyList(),
    ) : UiText {
        override val isEmpty: Boolean = false
    }
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value.orEmpty()
    is UiText.Resource -> stringResource(value, *args.toTypedArray())
}
