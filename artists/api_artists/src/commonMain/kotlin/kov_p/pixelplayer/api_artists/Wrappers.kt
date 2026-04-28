package kov_p.pixelplayer.api_artists

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import kov_p.pixelplayer.feature_artists.detail.ArtistDetailComposable
import kov_p.pixelplayer.feature_artists.ui.ArtistsScaffoldWrapper

@Composable
fun ArtistsComposableWrapper(onArtistClick: (artistId: String) -> Unit) {
    ArtistsScaffoldWrapper(onArtistClick = onArtistClick)
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
