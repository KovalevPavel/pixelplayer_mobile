plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature_tracks"))
        }
    }
}
