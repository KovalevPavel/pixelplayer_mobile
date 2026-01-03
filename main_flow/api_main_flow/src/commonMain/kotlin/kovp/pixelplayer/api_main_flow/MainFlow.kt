package kovp.pixelplayer.api_main_flow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import kovp.pixelplayer.feature_main_flow.MainFlowScreen
import kovp.pixelplayer.feature_main_flow.ui.AlbumsComposable
import kovp.pixelplayer.feature_main_flow.ui.ArtistsComposable
import kovp.pixelplayer.feature_main_flow.ui.TracksComposable

@Serializable
data class MainFlow(
    val token: String,
    val baseUrl: String,
)

fun NavGraphBuilder.registerMainFlow() {
    composable<MainFlow> { entry ->
        val route = entry.toRoute<MainFlow>()

        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .components {
                    add { chain ->
                        val newRequest = ImageRequest.Builder(chain.request)
                            .data(route.baseUrl+"/img/${chain.request.data}")
                            .build()

                        chain.withRequest(newRequest).proceed()
                    }
                }
                .build()
        }

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
