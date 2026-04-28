package kov_p.pixelplayer.feature_artists.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kov_p.pixelplayer.core.orZero
import kov_p.pixelplayer.core_design.AppPreview
import kov_p.pixelplayer.core_design.AppTheme
import kov_p.pixelplayer.core_design.pixelColors
import kov_p.pixelplayer.core_design.pixelTypography
import kov_p.pixelplayer.core_ui.UiText
import kov_p.pixelplayer.core_ui.CollectWithLifecycle
import kov_p.pixelplayer.core_ui.components.error_view.ErrorView
import kov_p.pixelplayer.core_ui.components.error_view.ErrorVs
import kov_p.pixelplayer.core_ui.components.image.ImageLoadingPlaceholder
import kov_p.pixelplayer.core_ui.components.image.PixelImage
import kov_p.pixelplayer.core_ui.components.vertical_card.VerticalCard
import kov_p.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kov_p.pixelplayer.feature_artists.di.ArtistDetailsScope
import kov_p.pixelplayer.feature_artists.di.ArtistsScope
import kov_p.pixelplayer.feature_artists.di.detailsModule
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.core.parameter.parametersOf
import pixelplayer.core_ui.generated.resources.Res
import pixelplayer.core_ui.generated.resources.albums
import pixelplayer.core_ui.generated.resources.retry
import pixelplayer.core_ui.generated.resources.tracks

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ArtistDetailComposable(
    artistId: String,
    onBackPress: () -> Unit,
    navigateToAlbum: (albumId: String) -> Unit,
) {
    val koin = getKoin()

    val scope = remember(artistId) {
        koin.loadModules(listOf(detailsModule))
        koin.getOrCreateScope<ArtistDetailsScope>("${ArtistDetailsScope}:$artistId")
    }

    scope.linkTo(koin.getScope(ArtistsScope.toString()))

    val viewModel: ArtistDetailViewModel = remember(artistId, scope) {
        scope.get { parametersOf(artistId) }
    }

    val handleBackPress = remember(scope, onBackPress) {
        {
            runCatching { scope.close() }
            onBackPress()
        }
    }

    BackHandler(onBack = handleBackPress)

    viewModel.eventsFlow.CollectWithLifecycle { event ->
        when (event) {
            is ArtistDetailEvent.NavigateToAlbum -> {
                navigateToAlbum(event.albumId)
            }
        }
    }

    val viewState by viewModel.stateFlow.collectAsState()

    ArtistDetailContent(
        viewState = viewState,
        onAction = viewModel::handleAction,
        onBackPress = handleBackPress,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistDetailContent(
    viewState: ArtistDetailState,
    onBackPress: () -> Unit,
    onAction: (ArtistDetailAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
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

        AnimatedContent(
            modifier = Modifier.fillMaxWidth().weight(1f),
            targetState = viewState,
            contentKey = { it::class },
        ) { st ->
            when (st) {
                is ArtistDetailState.Content -> {
                    ArtistDetailDataContent(
                        viewState = st,
                        onAction = onAction,
                    )
                }

                is ArtistDetailState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ErrorView(
                            viewState = ErrorVs(
                                message = UiText.Dynamic(st.message),
                                action = UiText.Resource(Res.string.retry),
                            ),
                            onActionClick = { onAction(ArtistDetailAction.FetchData) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistDetailDataContent(
    viewState: ArtistDetailState.Content,
    onAction: (ArtistDetailAction) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            bottom = 100.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        header(viewState)
        albums(viewState, onAction)
    }
}

private fun LazyGridScope.header(
    viewState: ArtistDetailState.Content,
) {
    item(span = { GridItemSpan(2) }) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            viewState.apply {
                DetailStateContent(
                    label = "artist_avatar",
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
                            url = it.avatar,
                        )
                    },
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TextOnTitle(
                        label = "artist_name",
                        style = pixelTypography.titleLarge.copy(color = pixelColors.onBackground),
                        text = { it.artistName },
                    )

                    TextOnTitle(
                        label = "artist_albums",
                        style = pixelTypography.titleMedium.copy(color = pixelColors.onBackground),
                        text = {
                            stringResource(Res.string.albums, it.albums.size)
                        },
                    )
                }
            }
        }
    }
}

private fun LazyGridScope.albums(
    viewState: ArtistDetailState.Content,
    onAction: (ArtistDetailAction) -> Unit,
) {
    when (viewState) {
        is ArtistDetailState.Data -> {
            items(items = viewState.albums, key = VerticalCardVs::id) { item ->
                VerticalCard(
                    viewState = item.copy(
                        description = UiText.Dynamic(
                            stringResource(
                                Res.string.tracks,
                                (item.description as? UiText.Dynamic)?.value?.toIntOrNull().orZero(),
                            )
                        )
                    ),
                    onClick = {
                        onAction(ArtistDetailAction.OnAlbumClick(item.id))
                    },
                )
            }
        }

        ArtistDetailState.Loading -> {
            repeat(2) {
                item {
                    ImageLoadingPlaceholder(
                        modifier = Modifier.fillMaxWidth()
                            .size(180.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistDetailState.Content.TextOnTitle(
    label: String,
    style: TextStyle,
    text: @Composable (ArtistDetailState.Data) -> String,
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

@Composable
private fun ArtistDetailState.Content.DetailStateContent(
    label: String,
    onLoading: @Composable () -> Unit,
    onData: @Composable (ArtistDetailState.Data) -> Unit,
) {
    AnimatedContent(
        targetState = this,
        label = label,
        contentAlignment = Alignment.Center,
    ) { st ->
        when (st) {
            is ArtistDetailState.Data -> onData(st)
            ArtistDetailState.Loading -> onLoading()
        }
    }
}

@AppPreview
@Composable
private fun ArtistDetailPreview(
    @PreviewParameter(ArtistDetailStateProvider::class) viewState: ArtistDetailState,
) {
    AppTheme {
        ArtistDetailContent(
            viewState = viewState,
            onAction = {},
            onBackPress = {},
        )
    }
}

private class ArtistDetailStateProvider : PreviewParameterProvider<ArtistDetailState> {
    override val values: Sequence<ArtistDetailState> = sequenceOf(
        ArtistDetailState.Loading,
        ArtistDetailState.Error(message = "Error message"),
        ArtistDetailState.Data(
            artistName = "Manowar",
            avatar = "",
            albums = List(5) {
                VerticalCardVs(
                    id = it.toString(),
                    imageUrl = "",
                    title = UiText.Dynamic("Album $it"),
                    description = UiText.Dynamic("42"),
                    tagline = UiText.Dynamic("2007"),
                )
            }
                .toImmutableList(),
        )
    )
}
