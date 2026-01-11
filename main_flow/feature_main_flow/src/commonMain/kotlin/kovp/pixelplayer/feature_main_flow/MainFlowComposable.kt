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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kovp.pixelplayer.api_albums.AlbumsComposableWrapper
import kovp.pixelplayer.api_artists.ArtistsComposableWrapper
import kovp.pixelplayer.api_tracks.TracksComposableWrapper

@Composable
fun MainFlowComposable(
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        var selectedTab: Int by rememberSaveable { mutableIntStateOf(0) }
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
            state = rememberPagerState(pageCount = { MainFlowScreen.entries.size }),
            userScrollEnabled = false,
            key = { MainFlowScreen.entries[selectedTab] },
        ) {
            when (MainFlowScreen.entries[selectedTab]) {
                MainFlowScreen.Artists -> {
                    ArtistsComposableWrapper()
                }

                MainFlowScreen.Albums -> {
                    AlbumsComposableWrapper(navController = navController)
                }

                MainFlowScreen.Tracks -> {
                    TracksComposableWrapper()
                }
            }
        }
    }
}
