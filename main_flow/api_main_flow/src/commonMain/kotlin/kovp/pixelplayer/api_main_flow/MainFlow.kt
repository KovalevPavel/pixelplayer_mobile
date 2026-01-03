package kovp.pixelplayer.api_main_flow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kovp.pixelplayer.feature_main_flow.MainFlowScreen
import kovp.pixelplayer.feature_main_flow.ui.AlbumsComposable
import kovp.pixelplayer.feature_main_flow.ui.ArtistsComposable
import kovp.pixelplayer.feature_main_flow.ui.TracksComposable

@Serializable
object MainFlow

fun NavGraphBuilder.registerMainFlow() {
    composable<MainFlow> {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = rememberPagerState(pageCount = { MainFlowScreen.entries.size }),
            key = { MainFlowScreen.entries[it] },
        ) {
            when (MainFlowScreen.entries[it]) {
                MainFlowScreen.Artists -> {
                    ArtistsComposable()
                }

                MainFlowScreen.Albums -> {
                    AlbumsComposable()
                }

                MainFlowScreen.Tracks -> {
                    TracksComposable()
                }
            }
        }
    }
}
