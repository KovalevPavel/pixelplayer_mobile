plugins {
    alias(libs.plugins.pixelplayer.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core_main_flow"))
            implementation(project(":feature_main_flow"))
            implementation(project(":network"))
            implementation(project(":core_player"))
            implementation(project(":api_artists"))
            implementation(project(":api_albums"))
            implementation(project(":api_tracks"))
        }
    }
}
