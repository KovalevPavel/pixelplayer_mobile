package kovp.pixelplayer.core_ui.components.image

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme

@Composable
fun ImageLoadingPlaceholder(
    modifier: Modifier = Modifier,
) {
    var endOffset by remember { mutableStateOf(0f) }
    var flag by remember { mutableStateOf(true) }
    val off by animateFloatAsState(
        targetValue = if (flag) -500f else endOffset,
        animationSpec = tween(800),
        finishedListener = {
            flag = !flag
        },
    )
    val tt = ShaderBrush(
        LinearGradientShader(
            from = Offset(off, 0f),
            to = Offset(500 + off, 0f),
            colors = listOf(
                MaterialTheme.colorScheme.surfaceDim,
                Color.White,
                MaterialTheme.colorScheme.surfaceDim,
            ),
        )
    )

    Box(
        modifier = modifier
            .onSizeChanged { intSize -> endOffset = intSize.width.toFloat() }
            .background(brush = tt),
    )

    DisposableEffect(Unit) {
        flag = !flag
        onDispose { }
    }
}

@AppPreview
@Composable
private fun ImageLoadingPreview() {
    AppTheme {
        ImageLoadingPlaceholder(modifier = Modifier.size(200.dp))
    }
}
