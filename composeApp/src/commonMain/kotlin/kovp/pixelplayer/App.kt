package kovp.pixelplayer

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kovp.pixelplayer.api_login.LoginFlow
import kovp.pixelplayer.api_login.registerLoginFlow
import kovp.pixelplayer.api_main_flow.MainFlow
import kovp.pixelplayer.api_main_flow.registerMainFlow
import kovp.pixelplayer.core_credentials.credentialsModule
import kovp.pixelplayer.core_design.AppTheme
import kovp.pixelplayer.core.context.AppContext
import kovp.pixelplayer.core.context.bindContext
import kovp.pixelplayer.core_storage.di.storageModule
import kovp.pixelplayer.core_ui.CollectWithLifecycle
import kovp.pixelplayer.di.mainModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App(
    ctx: AppContext,
) {
    KoinApplication(
        application = {
            bindContext(ctx)
            modules(
                storageModule,
                mainModule,
                credentialsModule,
            )
        },
    ) {
        AppTheme {
            val viewModel = koinViewModel<MainViewModel>()
            var checkResult: MainEvent.CheckResult? by rememberSaveable(
                viewModel,
                stateSaver = CheckResultStateSaver(),
            ) {
                mutableStateOf(null)
            }

            viewModel.event.CollectWithLifecycle { event ->
                when (event) {
                    is MainEvent.LaunchMainHost -> {
                        checkResult = event.result
                    }
                }
            }

            HostComposable(result = checkResult, context = ctx)
        }
    }
}

@Composable
private fun HostComposable(result: MainEvent.CheckResult?, context: AppContext) {
    val startDestination = when (result) {
        null -> return
        MainEvent.CheckResult.EmptyEndpoint,
        MainEvent.CheckResult.EmptyCreds,
        -> {
            LoginFlow
        }

        is MainEvent.CheckResult.OpenMain -> {
            MainFlow(
                token = result.token,
                baseUrl = result.endpoint,
            )
        }
    }

    val hostNavController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = hostNavController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } },
    ) {
        registerLoginFlow(
            endpointIsEmpty = result == MainEvent.CheckResult.EmptyEndpoint,
            navController = hostNavController,
            onTokenSaved = { token, endpoint ->
                hostNavController.popBackStack(
                    route = LoginFlow,
                    inclusive = true,
                    saveState = false,
                )
                MainFlow(
                    token = token,
                    baseUrl = endpoint,
                )
                    .let(hostNavController::navigate)
            },
        )
        registerMainFlow(ctx = context)
    }
}
