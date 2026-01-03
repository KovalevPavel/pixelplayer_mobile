plugins {
    alias(libs.plugins.pixelplayer.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.serialization)
        }
    }
}
