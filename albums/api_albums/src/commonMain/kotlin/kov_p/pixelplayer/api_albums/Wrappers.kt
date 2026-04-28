package kov_p.pixelplayer.api_albums

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.feature_albums.detail.AlbumDetailComposable
import kov_p.pixelplayer.feature_albums.list.ui.AlbumsScaffoldWrapper

@Composable
fun AlbumsComposableWrapper(onAlbumClick: (albumId: String) -> Unit) {
    AlbumsScaffoldWrapper(onAlbumClick = onAlbumClick)
}

@Composable
fun AlbumDetailsComposableWrapper(
    albumId: String,
    navController: NavController,
) {
    AlbumDetailComposable(
        albumId = albumId,
        onBackPress = { navController.navigateUp() },
    )
}

@Serializable
data class AlbumDetails(val id: String)
