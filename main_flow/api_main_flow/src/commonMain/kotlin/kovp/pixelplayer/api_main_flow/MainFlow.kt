package kovp.pixelplayer.api_main_flow

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import kovp.pixelplayer.api_albums.AlbumDetails
import kovp.pixelplayer.api_albums.AlbumDetailsComposableWrapper
import kovp.pixelplayer.api_main_flow.di.MainFlowScope
import kovp.pixelplayer.api_main_flow.di.mainFlowModule
import kovp.pixelplayer.core.context.AppContext
import kovp.pixelplayer.core_main_flow.LocalMainScope
import kovp.pixelplayer.core_player.PlayerViewModel
import kovp.pixelplayer.core_player.di.playerModule
import kovp.pixelplayer.feature_main_flow.MainFlowComposable
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
                .networkCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
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
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
            viewState = playerState,
            onAction = playerVm::handlePlayerAction,
        ) { pointerModifier ->
            CompositionLocalProvider(LocalMainScope provides mainScope) {
                val mainFlowController = rememberNavController()

                NavHost(
                    modifier = Modifier.fillMaxSize().then(pointerModifier),
                    navController = mainFlowController,
                    startDestination = MainFlowScreen.Companion.Host,
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { -it } },
                    popEnterTransition = { slideInHorizontally { -it } },
                    popExitTransition = { slideOutHorizontally { it } },
                ) {
                    composable<MainFlowScreen.Companion.Host> {
                        MainFlowComposable(navController = mainFlowController)
                    }

                    composable<AlbumDetails> { entry ->
                        val albumId = entry.toRoute<AlbumDetails>().id

                        AlbumDetailsComposableWrapper(
                            albumId = albumId,
                            navController = mainFlowController,
                        )
                    }
                }
            }
        }
    }
}
