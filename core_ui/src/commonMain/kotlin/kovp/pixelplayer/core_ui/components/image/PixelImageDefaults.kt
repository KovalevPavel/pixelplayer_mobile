package kovp.pixelplayer.core_ui.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter

internal object PixelImageDefaults {
    val loadPlaceholder: @Composable BoxScope.() -> Unit = {
        ImageLoadingPlaceholder(modifier = Modifier.matchParentSize())
    }

    val errorPlaceholder: @Composable BoxScope.() -> Unit = {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceDim)
                .matchParentSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier.fillMaxSize(.3f),
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
