plugins {
    alias(libs.plugins.pixelplayer.library)
    alias(libs.plugins.pixelplayer.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain_albums"))
            implementation(project(":core_main_flow"))
            implementation(project(":network"))
        }
    }
}
