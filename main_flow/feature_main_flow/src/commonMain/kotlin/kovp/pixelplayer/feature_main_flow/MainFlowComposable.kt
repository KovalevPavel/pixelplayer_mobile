package kovp.pixelplayer.feature_main_flow

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.compose.rememberNavController
import kovp.pixelplayer.api_albums.AlbumDetails
import kovp.pixelplayer.api_albums.AlbumsComposableWrapper
import kovp.pixelplayer.api_artists.ArtistDetails
import kovp.pixelplayer.api_artists.ArtistsComposableWrapper
import kovp.pixelplayer.api_settings.SettingsScreenWrapper
import kovp.pixelplayer.api_tracks.TracksComposableWrapper
import kovp.pixelplayer.core_main_flow.LocalMainScope
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialog
import kovp.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kovp.pixelplayer.feature_main_flow.presentation.MainFlowAction
import kovp.pixelplayer.feature_main_flow.presentation.MainFlowEvent
import kovp.pixelplayer.feature_main_flow.presentation.MainFlowViewModel
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
    val mainScope = LocalMainScope.current
    var messageDialog: MessageDialogVs? by rememberSaveable { mutableStateOf(null) }

    val viewModel: MainFlowViewModel = remember(mainScope) { mainScope.get() }

    viewModel.eventsFlow.CollectWithLifecycle { event ->
        when (event) {
            is MainFlowEvent.ShowMessageDialog -> messageDialog = event.viewState
        }
    }

    val rootNavController = rememberNavController()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            PrimaryTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedTabIndex,
            ) {
                MainFlowScreen.entries.forEachIndexed { i, t ->
                    val isSelected = i == selectedTabIndex
                    Tab(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                return@Tab
                            }
                            rootNavController.navigate(t.route)
                            selectedTabIndex = i
                        },
                        text = {
                            Text(
                                modifier = Modifier.basicMarquee(),
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
                startDestination = MainFlowScreen.entries.first().route,
            ) {
                registerRootTabs(
                    navController = navController,
                    onLogout = onLogout,
                )
            }
        }

        messageDialog?.let { vs ->
            MessageDialog(
                viewState = vs,
                removeFromComposition = { messageDialog = null },
            )
        }
    }

    LaunchedEffect(Unit) {
        MainFlowAction.CheckDemoAppNotice.let(viewModel::handleAction)
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
