plugins {
    alias(libs.plugins.pixelplayer.library)
    alias(libs.plugins.pixelplayer.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core_player"))
            implementation(project(":api_credentials"))
        }
    }
}
