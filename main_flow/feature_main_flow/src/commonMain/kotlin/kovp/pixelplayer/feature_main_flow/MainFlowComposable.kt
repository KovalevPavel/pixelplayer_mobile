package kovp.pixelplayer.feature_main_flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kovp.pixelplayer.api_albums.AlbumsComposableWrapper
import kovp.pixelplayer.api_artists.ArtistsComposableWrapper
import kovp.pixelplayer.api_settings.SettingsScreenWrapper
import kovp.pixelplayer.api_tracks.TracksComposableWrapper

@Composable
fun MainFlowComposable(
    navController: NavController,
    onLogout: () -> Unit,
) {
    var selectedTab: Int by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = selectedTab,
        pageCount = { MainFlowScreen.entries.size },
    )

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        PrimaryTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTab,
        ) {
            MainFlowScreen.entries.forEachIndexed { i, t ->
                Tab(
                    selected = i == selectedTab,
                    onClick = {
                        selectedTab = MainFlowScreen.entries.indexOf(t)
                    },
                    text = {
                        Text(text = t.name)
                    }
                )
            }
        }
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = false,
        ) { page ->
            when (MainFlowScreen.entries[page]) {
                MainFlowScreen.Artists -> {
                    ArtistsComposableWrapper(navController = navController)
                }

                MainFlowScreen.Albums -> {
                    AlbumsComposableWrapper(navController = navController)
                }

                MainFlowScreen.Tracks -> {
                    TracksComposableWrapper()
                }

                MainFlowScreen.Settings -> {
                    SettingsScreenWrapper(onLogout = onLogout)
                }
            }
        }
    }
}
