plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_tracks"))
            implementation(project(":feature_tracks"))
        }
    }
}
