package kovp.pixelplayer.feature_main_flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kovp.pixelplayer.api_albums.AlbumDetails
import kovp.pixelplayer.api_albums.AlbumsComposableWrapper
import kovp.pixelplayer.api_artists.ArtistDetails
import kovp.pixelplayer.api_artists.ArtistsComposableWrapper
import kovp.pixelplayer.api_settings.SettingsScreenWrapper
import kovp.pixelplayer.api_tracks.TracksComposableWrapper
import org.jetbrains.compose.resources.stringResource
import pixelplayer.feature_main_flow.generated.resources.Res
import pixelplayer.feature_main_flow.generated.resources.tab_albums
import pixelplayer.feature_main_flow.generated.resources.tab_artists
import pixelplayer.feature_main_flow.generated.resources.tab_settings
import pixelplayer.feature_main_flow.generated.resources.tab_tracks

@Composable
fun MainFlowComposable(
    navController: NavController,
    onLogout: () -> Unit,
) {
    val rootNavController = rememberNavController()
    val currentEntry by rootNavController.currentBackStackEntryAsState()
    val restoredSelectedTab = currentEntry?.destination?.route?.let(MainFlowScreen::fromRoute)

    var selectedTabRoute by rememberSaveable { mutableStateOf(MainFlowScreen.Artists.route) }
    val selectedTab = restoredSelectedTab
        ?: MainFlowScreen.fromRoute(selectedTabRoute)
        ?: MainFlowScreen.Artists

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        PrimaryTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = MainFlowScreen.entries.indexOf(selectedTab),
        ) {
            MainFlowScreen.entries.forEach { t ->
                Tab(
                    selected = t == selectedTab,
                    onClick = {
                        if (t == selectedTab) {
                            return@Tab
                        }
                        selectedTabRoute = t.route
                        rootNavController.navigate(t.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(MainFlowScreen.Artists.route) {
                                saveState = true
                            }
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(t.titleRes),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }

        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = rootNavController,
            startDestination = selectedTabRoute,
        ) {
            registerRootTabs(
                navController = navController,
                onLogout = onLogout,
            )
        }
    }
}

private val MainFlowScreen.titleRes
    get() = when (this) {
        MainFlowScreen.Artists -> Res.string.tab_artists
        MainFlowScreen.Albums -> Res.string.tab_albums
        MainFlowScreen.Tracks -> Res.string.tab_tracks
        MainFlowScreen.Settings -> Res.string.tab_settings
    }

private fun NavGraphBuilder.registerRootTabs(
    navController: NavController,
    onLogout: () -> Unit,
) {
    composable(route = MainFlowScreen.Artists.route) {
        ArtistsComposableWrapper { artistId ->
            ArtistDetails(artistId).let(navController::navigate)
        }
    }

    composable(route = MainFlowScreen.Albums.route) {
        AlbumsComposableWrapper { albumId ->
            AlbumDetails(id = albumId).let(navController::navigate)
        }
    }

    composable(route = MainFlowScreen.Tracks.route) {
        TracksComposableWrapper()
    }

    composable(route = MainFlowScreen.Settings.route) {
        SettingsScreenWrapper(
            onLogout = onLogout,
        )
    }
}
