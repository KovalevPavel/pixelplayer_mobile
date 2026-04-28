package kov_p.pixelplayer.feature_main_flow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import kov_p.pixelplayer.api_albums.AlbumDetails
import kov_p.pixelplayer.api_albums.AlbumsComposableWrapper
import kov_p.pixelplayer.api_artists.ArtistDetails
import kov_p.pixelplayer.api_artists.ArtistsComposableWrapper
import kov_p.pixelplayer.api_settings.SettingsScreenWrapper
import kov_p.pixelplayer.api_tracks.TracksComposableWrapper
import kov_p.pixelplayer.core_main_flow.LocalMainScope
import kov_p.pixelplayer.core_ui.CollectWithLifecycle
import kov_p.pixelplayer.core_ui.components.message_dialog.MessageDialog
import kov_p.pixelplayer.core_ui.components.message_dialog.MessageDialogVs
import kov_p.pixelplayer.feature_main_flow.presentation.MainFlowAction
import kov_p.pixelplayer.feature_main_flow.presentation.MainFlowEvent
import kov_p.pixelplayer.feature_main_flow.presentation.MainFlowViewModel
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

            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = MainFlowScreen.entries[selectedTabIndex],
                transitionSpec = { pagerTabTransition() },
                label = "main_flow_tabs",
            ) { screen ->
                RootTabContent(
                    modifier = Modifier.fillMaxSize(),
                    screen = screen,
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

private fun AnimatedContentTransitionScope<MainFlowScreen>.pagerTabTransition(): ContentTransform {
    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1

    return slideInHorizontally(
        animationSpec = tween(TAB_ANIMATION_DURATION_MS),
        initialOffsetX = { it * direction },
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(TAB_ANIMATION_DURATION_MS),
        targetOffsetX = { -it * direction },
    ) using SizeTransform(clip = true)
}

@Composable
private fun RootTabContent(
    modifier: Modifier = Modifier,
    screen: MainFlowScreen,
    navController: NavController,
    onLogout: () -> Unit,
) {
    Box(modifier = modifier) {
        when (screen) {
            MainFlowScreen.Artists -> {
                ArtistsComposableWrapper { artistId ->
                    ArtistDetails(artistId).let(navController::navigate)
                }
            }

            MainFlowScreen.Albums -> {
                AlbumsComposableWrapper { albumId ->
                    AlbumDetails(id = albumId).let(navController::navigate)
                }
            }

            MainFlowScreen.Tracks -> {
                TracksComposableWrapper()
            }

            MainFlowScreen.Settings -> {
                SettingsScreenWrapper(
                    onLogout = onLogout,
                )
            }
        }
    }
}

private const val TAB_ANIMATION_DURATION_MS = 300
