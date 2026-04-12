plugins {
    alias(libs.plugins.pixelplayer.library)
    alias(libs.plugins.pixelplayer.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":api_credentials"))
            implementation(project(":api_storage"))
            implementation(project(":core_main_flow"))
            implementation(project(":network"))
            implementation(project(":api_artists"))
            implementation(project(":api_albums"))
            implementation(project(":api_tracks"))
            implementation(project(":api_settings"))
        }
    }
}
