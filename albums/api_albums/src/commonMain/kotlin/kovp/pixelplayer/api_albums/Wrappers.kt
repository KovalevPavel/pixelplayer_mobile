package kovp.pixelplayer.api_albums

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kovp.pixelplayer.feature_albums.detail.AlbumDetailComposable
import kovp.pixelplayer.feature_albums.list.ui.AlbumsScaffoldWrapper

@Composable
fun AlbumsComposableWrapper(navController: NavController) {
    AlbumsScaffoldWrapper {
        navController.navigate(AlbumDetails(it))
    }
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
