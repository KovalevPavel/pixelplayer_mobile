package kovp.pixelplayer.api_login.di

import kov_p.pixelplayer.network.di.bindUnauthorizedClient
import kov_p.pixelplayer.network.di.unauthorizedClient
import kovp.pixelplayer.domain_login.LoginRepository
import kovp.pixelplayer.feature_login.LoginViewModel
import kovp.pixelplayer.feature_login.data.LoginRepositoryImpl
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.bind
import org.koin.dsl.module

object LoginScope

val loginFlowModule = module {
    scope<LoginScope> {
        bindUnauthorizedClient()
        scoped {
            LoginRepositoryImpl(
                client = get(qualifier = unauthorizedClient),
                credentialsRepository = get(),
            )
        }
            .bind<LoginRepository>()
        scopedOf(::LoginViewModel)
    }
}
