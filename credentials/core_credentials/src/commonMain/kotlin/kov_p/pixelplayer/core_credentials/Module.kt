package kov_p.pixelplayer.core_credentials

import kov_p.pixelplayer.api_credentials.CredentialsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val credentialsModule = module {
    singleOf(::CredentialsRepositoryImpl)
        .bind<CredentialsRepository>()
}
