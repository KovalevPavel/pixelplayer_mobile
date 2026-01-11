package kovp.pixelplayer.feature_albums.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import kovp.pixelplayer.core_design.AppPreview
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core_ui.AnimationUtils
import kovp.pixelplayer.core_ui.LocalAnimationProvider
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCard
import kovp.pixelplayer.core_ui.components.vertical_card.VerticalCardVs
import kovp.pixelplayer.feature_albums.detail.AlbumDetailComposable
import kovp.pixelplayer.feature_albums.detail.DetailDto
import kovp.pixelplayer.feature_albums.presentation.AlbumsAction
import kovp.pixelplayer.feature_albums.presentation.AlbumsState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun AlbumsList(
    state: AlbumsState.List,
    handleAction: (AlbumsAction) -> Unit,
) {
    var selectedAlbum: DetailDto? by rememberSaveable(saver = DetailDtoSaver()) {
        mutableStateOf(null)
    }

    SharedTransitionScope { m ->
        AnimatedContent(
            modifier = m,
            targetState = selectedAlbum,
            label = "album_to_details",
            transitionSpec = {
                when (targetState) {
                    null -> {
                        (slideIn { (width, height) -> IntOffset(x = -width, y = 0) })
                            .togetherWith(slideOut { (width, _) ->
                                IntOffset(x = width, y = 0)
                            })
                    }

                    else -> {
                        (slideIn { (width, height) -> IntOffset(x = width, y = 0) })
                            .togetherWith(slideOut { (width, _) ->
                                IntOffset(x = -width, y = 0)
                            })
                    }
                }
            }
        ) { st ->
            CompositionLocalProvider(
                LocalAnimationProvider provides AnimationUtils(
                    sharedTransitionScope = this@SharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            ) {
                when (st) {
                    null -> LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
                    ) {
                        items(items = state.albums, key = VerticalCardVs::id) { item ->
                            VerticalCard(
                                viewState = item,
                                onClick = {
                                    selectedAlbum = DetailDto(
                                        albumId = item.id,
                                        title = item.title,
                                        artist = item.description,
                                        cover = item.imageUrl,
                                        year = item.tagline.orEmpty(),
                                    )
                                },
                            )
                        }
                    }

                    else -> {
                        AlbumDetailComposable(
                            dto = st,
                            onBackPress = { selectedAlbum = null }
                        )
                    }
                }
            }
        }
    }
}

private class DetailDtoSaver : Saver<MutableState<DetailDto?>, Map<String, String?>> {
    override fun restore(value: Map<String, String?>): MutableState<DetailDto?> {
        return if (value.isEmpty()) {
            mutableStateOf(null)
        } else {
            mutableStateOf(
                DetailDto(
                    albumId = value[ALBUM_ID] ?: return mutableStateOf(null),
                    title = value[TITLE].toString(),
                    artist = value[ARTIST].toString(),
                    cover = value[COVER].toString(),
                    year = value[YEAR].toString(),
                ),
            )
        }
    }

    override fun SaverScope.save(value: MutableState<DetailDto?>): Map<String, String?> {
        return mapOf(
            ALBUM_ID to value.value?.albumId,
            TITLE to value.value?.title,
            ARTIST to value.value?.artist,
            COVER to value.value?.cover,
            YEAR to value.value?.year,
        )
    }

    companion object {
        private const val ALBUM_ID = "ALBUM_ID"
        private const val TITLE = "TITLE"
        private const val ARTIST = "ARTIST"
        private const val COVER = "COVER"
        private const val YEAR = "YEAR"
    }
}

@AppPreview
@Composable
private fun AlbumsListPreview(
    @PreviewParameter(ArtistsListProvider::class) state: AlbumsState.List,
) {
    AppTheme {
        AlbumsList(state = state, handleAction = {})
    }
}

private class ArtistsListProvider : PreviewParameterProvider<AlbumsState.List> {
    override val values: Sequence<AlbumsState.List> = List(2) { i ->
        AlbumsState.List(
            albums = List((i + 1) * 3) {
                VerticalCardVs(
                    id = it.toString(),
                    title = "Album $it ".repeat(it + 1),
                    imageUrl = "",
                    description = "2007",
                )
            }
                .toImmutableList()
        )
    }
        .asSequence()
}
