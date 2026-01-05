plugins {
    alias(libs.plugins.pixelplayer.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.media3.player)
            implementation(libs.media3.datasource)
            implementation(libs.media3.session)
        }
    }
}
