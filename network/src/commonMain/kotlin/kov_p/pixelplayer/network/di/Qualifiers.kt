package kov_p.pixelplayer.network.di

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue

val unauthorizedClient = object : Qualifier {
    override val value: QualifierValue = "unauthorizedClient"
}

val authorizedClient = object : Qualifier {
    override val value: QualifierValue = "authorizedClient"
}
