package kovp.pixelplayer.api_artists

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kovp.pixelplayer.feature_artists.detail.ArtistDetailComposable
import kovp.pixelplayer.feature_artists.ui.ArtistsScaffoldWrapper

@Composable
fun ArtistsComposableWrapper(navController: NavController) {
    ArtistsScaffoldWrapper { artistId ->
        navController.navigate(ArtistDetails(artistId))
    }
}

@Composable
fun ArtistDetailsComposableWrapper(
    artistId: String,
    navController: NavController,
    navigateToAlbum: (albumId: String) -> Unit,
) {
    ArtistDetailComposable(
        artistId = artistId,
        onBackPress = { navController.navigateUp() },
        navigateToAlbum = navigateToAlbum,
    )
}

@Serializable
data class ArtistDetails(val artistId: String)
