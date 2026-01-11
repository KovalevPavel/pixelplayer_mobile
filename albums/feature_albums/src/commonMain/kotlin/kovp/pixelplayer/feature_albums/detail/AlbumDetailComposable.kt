package kovp.pixelplayer.feature_albums.detail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppTypography
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCard
import kovp.pixelplayer.core_ui.components.image.ImageLoadingPlaceholder
import kovp.pixelplayer.core_ui.components.image.PixelImage
import kovp.pixelplayer.core_ui.withAnimation
import kovp.pixelplayer.feature_albums.di.AlbumDetailsScope
import kovp.pixelplayer.feature_albums.di.AlbumsScope
import kovp.pixelplayer.feature_albums.di.detailsModule
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumDetailComposable(
    dto: DetailDto,
    onBackPress: () -> Unit,
) {
    BackHandler { onBackPress() }

    val koin = getKoin()

    val scope = remember {
        koin.loadModules(listOf(detailsModule))
        koin.getOrCreateScope<AlbumDetailsScope>(AlbumDetailsScope.toString())
    }

    scope.linkTo(koin.getScope(AlbumsScope.toString()))

    val viewModel: AlbumDetailViewModel = remember {
        scope.get { parametersOf(dto.albumId) }
    }

    DisposableEffect(Unit) {
        onDispose { scope.close() }
    }

    val viewState = viewModel.viewState

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PixelImage(
                    modifier = Modifier.withAnimation(key = dto.cover)
                        .size(180.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    url = dto.cover,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        modifier = Modifier.withAnimation(key = "${dto.albumId}_${dto.title}"),
                        text = dto.title,
                        style = AppTypography.titleMedium,
                    )

                    Text(
                        modifier = Modifier.withAnimation(key = "${dto.albumId}_${dto.artist}"),
                        text = dto.artist,
                        style = AppTypography.bodyMedium,
                    )

                    Text(
                        modifier = Modifier.withAnimation(key = "${dto.albumId}_${dto.year}"),
                        text = dto.year,
                        style = AppTypography.bodySmall,
                    )
                }
            }
        }

        if (viewState is AlbumDetailState.Data) {
            itemsIndexed(items = viewState.tracks, key = { _, item -> item.id }) { i, item ->
                HorizontalCard(
                    modifier = Modifier.fillMaxWidth(),
                    viewState = item,
                    onClick = {
                        AlbumDetailAction.OnTrackClick(index = i).let(viewModel::handleAction)
                    },
                )
            }
        } else {
            repeat(2) {
                item {
                    ImageLoadingPlaceholder(
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
            }
        }
    }
}
