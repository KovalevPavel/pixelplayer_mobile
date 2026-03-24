package kovp.pixelplayer.feature_albums.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_design.pixelColors
import kovp.pixelplayer.core_design.pixelTypography
import kovp.pixelplayer.core_player.PlayerVs
import kovp.pixelplayer.core_ui.components.image.ImageLoadingPlaceholder
import kovp.pixelplayer.core_ui.components.image.PixelImage
import kovp.pixelplayer.domain_albums.AlbumVo
import kovp.pixelplayer.feature_albums.di.AlbumDetailsScope
import kovp.pixelplayer.feature_albums.di.AlbumsScope
import kovp.pixelplayer.feature_albums.di.detailsModule
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class)
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

    scope.linkTo(koin.getOrCreateScope<AlbumsScope>(AlbumsScope.toString()))

    val viewModel: AlbumDetailViewModel = remember {
        scope.get { parametersOf(albumId) }
    }

    AlbumDetailsContent(
        viewState = viewModel.viewState,
        onBackPress = {
            onBackPress()
            scope.close()
        },
        playerStateFlow = viewModel.playerVs,
        onAction = viewModel::handleAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumDetailsContent(
    viewState: AlbumDetailState,
    playerStateFlow: StateFlow<PlayerVs>,
    onBackPress: () -> Unit,
    onAction: (AlbumDetailAction) -> Unit,
) {
    val playerState by playerStateFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(pixelColors.background)) {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(containerColor = pixelColors.background),
            navigationIcon = {
                IconButton(onClick = onBackPress) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(
                top = 32.dp,
                bottom = 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                AlbumHeader(
                    modifier = Modifier.padding(bottom = 24.dp),
                    state = viewState,
                )
            }

            if (viewState is AlbumDetailState.Data) {
                viewState.disks.forEach { disk ->
                    disk.diskNumber.takeIf { viewState.disks.size > 1 }?.let { n ->
                        stickyHeader {
                            Text(
                                modifier = Modifier.fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                pixelColors.surfaceContainer,
                                            ),
                                        ),
                                    )
                                    .padding(end = 16.dp)
                                    .padding(vertical = 8.dp),
                                text = "Disk $n",
                                style = pixelTypography.titleSmall,
                                color = pixelColors.onSurfaceVariant,
                                textAlign = TextAlign.End,
                            )
                        }
                    }

                    itemsIndexed(items = disk.tracks, key = { _, item -> item.id }) { i, item ->
                        TrackCardComposable(
                            viewState = item,
                            currentPlaying = playerState.trackId,
                            isPlaying = (playerState as? PlayerVs.Data)?.timeLine?.isPlaying == true,
                            onTrackClick = {
                                onAction(AlbumDetailAction.OnTrackClick(index = item.globalPosition))
                            },
                        )

                        if (i != disk.tracks.lastIndex) {
                            Box(
                                modifier = Modifier.padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .height(DividerDefaults.Thickness)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                DividerDefaults.color,
                                            )
                                        )
                                    ),
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                repeat(2) {
                    item {
                        ImageLoadingPlaceholder(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(50.dp)
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
    modifier: Modifier,
    state: AlbumDetailState,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    style = pixelTypography.titleLarge.copy(color = pixelColors.onBackground),
                    text = (AlbumDetailState.Data::title),
                )

                TextOnTitle(
                    label = "album_artist",
                    style = pixelTypography.titleMedium.copy(color = pixelColors.onBackground),
                    text = (AlbumDetailState.Data::artist),
                )

                TextOnTitle(
                    label = "album_year",
                    style = pixelTypography.bodyMedium.copy(color = pixelColors.onBackground),
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
        contentAlignment = Alignment.Center,
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

@AppPreview
@Composable
private fun AlbumDetailsComposablePreview(
    @PreviewParameter(AlbumDetailPreviewProvider::class) viewState: AlbumDetailState,
) {
    AppTheme {
        AlbumDetailsContent(
            viewState = viewState,
            playerStateFlow = MutableStateFlow(PlayerVs.Empty),
            onBackPress = {},
            onAction = {},
        )
    }
}

private class AlbumDetailPreviewProvider : PreviewParameterProvider<AlbumDetailState> {
    override val values: Sequence<AlbumDetailState> = sequenceOf(
        AlbumDetailState.Loading,
        AlbumDetailState.Data(
            title = "Battle Hymns",
            artist = "Manowar",
            cover = "",
            year = "1982",
            disks = List(2) { d ->
                AlbumDetailState.Disk(
                    diskNumber = d,
                    tracks = List(5 * (d + 1)) {
                        AlbumDetailState.TrackVs(
                            id = it.toString(),
                            title = "Track title $it",
                            position = it,
                            duration = "3:53",
                            artist = "Manowar",
                            quality = AlbumVo.Quality.Lossless,
                            globalPosition = it,
                        )
                    }
                        .toImmutableList(),
                )
            }
                .toImmutableList(),
        ),
    )
}
