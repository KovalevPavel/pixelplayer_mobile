package kovp.pixelplayer.api_main_flow

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kovp.pixelplayer.api_albums.AlbumsComposableWrapper
import kovp.pixelplayer.api_artists.ArtistsComposableWrapper
import kovp.pixelplayer.api_main_flow.di.MainFlowScope
import kovp.pixelplayer.api_main_flow.di.mainFlowModule
import kovp.pixelplayer.api_tracks.TracksComposableWrapper
import kovp.pixelplayer.core.context.AppContext
import kovp.pixelplayer.core_main_flow.LocalMainScope
import kovp.pixelplayer.core_player.PlayerViewModel
import kovp.pixelplayer.core_player.di.playerModule
import kovp.pixelplayer.feature_main_flow.MainFlowScreen
import org.koin.compose.getKoin

@Serializable
data class MainFlow(
    val token: String,
    val baseUrl: String,
)

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.registerMainFlow(ctx: AppContext) {
    composable<MainFlow> { entry ->
        val route = entry.toRoute<MainFlow>()
        val koin = getKoin()

        val mainScope = remember {
            koin.loadModules(
                listOf(
                    mainFlowModule(token = route.token, baseUrl = route.baseUrl),
                    playerModule(ctx = ctx, token = route.token, baseUrl = route.baseUrl),
                ),
            )
            koin.getOrCreateScope<MainFlowScope>(MainFlowScope.toString())
        }

        val playerVm: PlayerViewModel = remember { koin.get() }
        val playerState by playerVm.playerVs.collectAsState()

        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .components {
                    add { chain ->
                        val newRequest = ImageRequest.Builder(chain.request)
                            .data(route.baseUrl + "/img/${chain.request.data}")
                            .httpHeaders(
                                NetworkHeaders.Builder()
                                    .set("Authorization", route.token)
                                    .build(),
                            )
                            .build()

                        chain.withRequest(newRequest).proceed()
                    }
                }
                .build()
        }

        PlayerScaffold(
            viewState = playerState,
        ) { paddingValues, bottomSheetState ->
            val scope = rememberCoroutineScope()
            HorizontalPager(
                modifier = Modifier.padding(paddingValues).fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitPointerEvent()
                            scope.launch {
                                bottomSheetState.partialExpand()
                            }
                        }
                    },
                state = rememberPagerState(pageCount = { MainFlowScreen.entries.size }),
                key = { MainFlowScreen.entries[it] },
            ) {
                CompositionLocalProvider(LocalMainScope provides mainScope) {
                    when (MainFlowScreen.entries[it]) {
                        MainFlowScreen.Artists -> {
                            ArtistsComposableWrapper()
                        }

                        MainFlowScreen.Albums -> {
                            AlbumsComposableWrapper()
                        }

                        MainFlowScreen.Tracks -> {
                            TracksComposableWrapper()
                        }
                    }
                }
            }
        }
    }
}
