package kovp.pixelplayer.feature_albums.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kovp.pixelplayer.core_design.AppTypography
import kovp.pixelplayer.core_ui.components.horizontal_card.HorizontalCard
import kovp.pixelplayer.core_ui.components.image.ImageLoadingPlaceholder
import kovp.pixelplayer.core_ui.components.image.PixelImage
import kovp.pixelplayer.feature_albums.di.AlbumDetailsScope
import kovp.pixelplayer.feature_albums.di.AlbumsScope
import kovp.pixelplayer.feature_albums.di.detailsModule
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailComposable(
    albumId: String,
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
        scope.get { parametersOf(albumId) }
    }

    DisposableEffect(Unit) {
        onDispose { scope.close() }
    }

    val viewState = viewModel.viewState

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBackPress,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AlbumHeader(state = viewState)
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
}

@Composable
private fun AlbumHeader(
    state: AlbumDetailState,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        state.apply {
            DetailStateContent(
                label = "album_image",
                onLoading = {
                    ImageLoadingPlaceholder(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                },
                onData = {
                    PixelImage(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        url = it.cover,
                    )
                },
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TextOnTitle(
                    label = "album_title",
                    style = AppTypography.titleLarge,
                    text = (AlbumDetailState.Data::title),
                )

                TextOnTitle(
                    label = "album_artist",
                    style = AppTypography.titleMedium,
                    text = (AlbumDetailState.Data::artist),
                )

                TextOnTitle(
                    label = "album_year",
                    style = AppTypography.bodyMedium,
                    text = (AlbumDetailState.Data::year),
                )
            }
        }

    }
}

@Composable
private fun AlbumDetailState.DetailStateContent(
    label: String,
    onLoading: @Composable () -> Unit,
    onData: @Composable (AlbumDetailState.Data) -> Unit,
) {
    AnimatedContent(
        targetState = this,
        label = label,
    ) { st ->
        when (st) {
            is AlbumDetailState.Data -> onData(st)
            AlbumDetailState.Loading -> onLoading()
        }
    }
}

@Composable
private fun AlbumDetailState.TextOnTitle(
    label: String,
    style: TextStyle,
    text: (AlbumDetailState.Data) -> String,
) {
    val measurer = rememberTextMeasurer()
    DetailStateContent(
        label = label,
        onLoading = {
            val height = LocalDensity.current.run {
                measurer.measure(text = "", style = style)
                    .size
                    .height
                    .toDp()
            }

            ImageLoadingPlaceholder(modifier = Modifier.width(120.dp).height(height))
        },
        onData = {
            Text(
                text = text(it),
                style = style,
            )
        },
    )
}
