package kovp.pixelplayer.api_main_flow.di

import kov_p.pixelplayer.network.di.bindAuthorizedClient
import org.koin.dsl.module

object MainFlowScope

fun mainFlowModule(token: String, baseUrl: String) = module {
    scope<MainFlowScope> {
        bindAuthorizedClient(token = token, baseUrl = baseUrl)
    }
}
