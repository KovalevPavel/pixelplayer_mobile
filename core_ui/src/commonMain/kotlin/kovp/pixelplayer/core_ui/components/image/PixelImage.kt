package kovp.pixelplayer.core_ui.components.image

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage

@Composable
fun PixelImage(
    modifier: Modifier = Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    loading: @Composable BoxScope.() -> Unit = PixelImageDefaults.loadPlaceholder,
    error: @Composable BoxScope.() -> Unit = PixelImageDefaults.errorPlaceholder,
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = url,
        contentScale = contentScale,
        contentDescription = contentDescription,
        error = { error() },
        loading = { loading() },
    )
}
